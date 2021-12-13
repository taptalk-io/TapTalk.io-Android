package io.taptalk.TapTalk.Listener;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapSendMessageInterface;
import io.taptalk.TapTalk.Model.TAPMessageModel;

@Keep
public abstract class TapCoreSendMessageListener implements TapSendMessageInterface {

    @Override
    public void onStart(TAPMessageModel message) {

    }

    @Override
    public void onSuccess(TAPMessageModel message) {

    }

    @Override
    public void onError(@Nullable TAPMessageModel message, String errorCode, String errorMessage) {

    }

    @Override
    public void onProgress(TAPMessageModel message, int percentage, long bytes) {

    }
}
