package org.team_trk.messages;

import java.util.Vector;

public class PreparationNotification extends GenericGuidMessage {

    public PreparationNotification(Vector<String> guids, String productType) {
        super(guids, productType);
    }
}
