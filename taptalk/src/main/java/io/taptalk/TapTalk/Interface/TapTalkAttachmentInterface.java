package io.taptalk.TapTalk.Interface;

public interface TapTalkAttachmentInterface {
    void onDocumentSelected();
    void onCameraSelected();
    void onGallerySelected();
    void onAudioSelected();
    void onLocationSelected();
    void onContactSelected();
    void onCopySelected(String text);
    void onReplySelected();
    void onForwardSelected();
    void onOpenLinkSelected();
    void onComposeSelected();
    void onPhoneCallSelected();
    void onPhoneSmsSelected();
}
