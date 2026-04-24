package com.example.seproject;

public class RecentActivityItem {
    public static final int TYPE_ACTIVE_VISITOR = 1;
    public static final int TYPE_ACTIVE_EMERGENCY = 2;

    private final int type;
    private final String documentId;
    private final String primaryId;
    private final String titleValue;
    private final String subtitleValue;
    private final long eventTimeMillis;

    public RecentActivityItem(int type, String documentId, String primaryId, String titleValue,
                              String subtitleValue, long eventTimeMillis) {
        this.type = type;
        this.documentId = documentId;
        this.primaryId = primaryId;
        this.titleValue = titleValue;
        this.subtitleValue = subtitleValue;
        this.eventTimeMillis = eventTimeMillis;
    }

    public int getType() {
        return type;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public String getTitleValue() {
        return titleValue;
    }

    public String getSubtitleValue() {
        return subtitleValue;
    }

    public long getEventTimeMillis() {
        return eventTimeMillis;
    }
}
