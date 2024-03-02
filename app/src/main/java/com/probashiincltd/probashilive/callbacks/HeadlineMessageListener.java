package com.probashiincltd.probashilive.callbacks;

import org.jivesoftware.smack.packet.Stanza;

public interface HeadlineMessageListener {
    void onHeadlineMessage(Stanza message);
}
