package com.epam.digital.data.platform.history.service;

import com.epam.digital.data.platform.excerpt.model.ExcerptEventDto;
import com.epam.digital.data.platform.excerpt.model.ExcerptProcessingStatus;
import com.epam.digital.data.platform.excerpt.model.StatusDto;
import com.epam.digital.data.platform.history.exception.HistoryExcerptGenerationException;
import com.epam.digital.data.platform.history.util.ThirdPartyHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ExcerptService {

  private static final long EXCERPT_STATUS_CHECK_TIMEOUT = 5;

  private final int excerptStatusCheckMaxAttempts;

  private final Logger log = LoggerFactory.getLogger(ExcerptService.class);

  private final ExcerptRestClient excerptRestClient;
  private final DigitalSignatureService digitalSignatureService;
  private final ThreadSleepService threadSleepService;

  public ExcerptService(
      @Value("${excerpt.statusCheck.maxAttempts}") int excerptStatusCheckMaxAttempts,
      ExcerptRestClient excerptRestClient,
      DigitalSignatureService digitalSignatureService,
      ThreadSleepService threadSleepService) {
    this.excerptStatusCheckMaxAttempts = excerptStatusCheckMaxAttempts;
    this.excerptRestClient = excerptRestClient;
    this.digitalSignatureService = digitalSignatureService;
    this.threadSleepService = threadSleepService;
  }

  public UUID generate(ExcerptEventDto excerptEventDto) {
    var derivedSignature = digitalSignatureService.sign(excerptEventDto);
    var derivedSignatureCephKey = digitalSignatureService.saveSignature(derivedSignature);
    Map<String, Object> excerptGenerateHeaders =
            createExcerptRequestHeaders(derivedSignatureCephKey);
    log.info("Calling excerpt generation");
    var excerptId =
        excerptRestClient.generate(excerptEventDto, excerptGenerateHeaders).getExcerptIdentifier();
    log.debug("Excerpt id: {}", excerptId);
    return excerptId;
  }

  public StatusDto getFinalProcessingStatus(UUID excerptId) throws InterruptedException {
    for (int i = 0; i < excerptStatusCheckMaxAttempts; i++) {
      StatusDto excerptStatus = getCurrentExcerptStatus(excerptId);
      if (!ExcerptProcessingStatus.IN_PROGRESS.equals(excerptStatus.getStatus())) {
        return excerptStatus;
      }
      log.info("Waiting {} seconds for excerpt result", EXCERPT_STATUS_CHECK_TIMEOUT);
      threadSleepService.sleep(EXCERPT_STATUS_CHECK_TIMEOUT);
    }
    throw new HistoryExcerptGenerationException(
        String.format(
            "Excerpt processing doesn't finished after %d status checks, failing by timeout",
                excerptStatusCheckMaxAttempts));
  }

  private Map<String, Object> createExcerptRequestHeaders(String derivedSignatureCephKey) {
    Map<String, Object> excerptGenerateHeaders = new HashMap<>();
    excerptGenerateHeaders.put(
        ThirdPartyHeader.X_DIGITAL_SIGNATURE.getHeaderName(), derivedSignatureCephKey);
    excerptGenerateHeaders.put(
        ThirdPartyHeader.X_DIGITAL_SIGNATURE_DERIVED.getHeaderName(), derivedSignatureCephKey);
    return excerptGenerateHeaders;
  }

  private StatusDto getCurrentExcerptStatus(UUID excerptId) {
    var excerptStatusDto = excerptRestClient.status(excerptId);
    log.info("Current excerpt status: {}", excerptStatusDto.getStatus());
    return excerptStatusDto;
  }
}
