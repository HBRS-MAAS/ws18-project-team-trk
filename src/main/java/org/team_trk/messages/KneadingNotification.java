package org.team_trk.messages;

import java.util.Vector;

public class KneadingNotification extends GenericGuidMessage {

    public KneadingNotification(Vector<String> guids, String productType) {
        super(guids, productType);
    }
}
