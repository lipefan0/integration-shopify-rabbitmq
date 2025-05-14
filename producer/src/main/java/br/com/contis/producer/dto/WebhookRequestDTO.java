package br.com.contis.producer.dto;

public class WebhookRequestDTO {
    private WebhookPayloadDTO data;
    public WebhookPayloadDTO getData() { return data; }
    public void setData(WebhookPayloadDTO data) { this.data = data; }
}
