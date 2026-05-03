package com.example.seproject;

import com.example.seproject.admin.AuditLogItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

public class SecurityAuditReportTest {

    @Test
    public void entry_factoryMethod_returnsTypeEntry() {
        AuditLogItem item = AuditLogItem.entry("P-001", "Ali Raza", "uid1", "Guard Ahmed", 1_000L, "Meeting");
        assertEquals(AuditLogItem.TYPE_ENTRY, item.getType());
    }

    @Test
    public void exit_factoryMethod_returnsTypeExit() {
        AuditLogItem item = AuditLogItem.exit("P-001", "Ali Raza", "uid1", "Guard Ahmed", 2_000L, "Meeting");
        assertEquals(AuditLogItem.TYPE_EXIT, item.getType());
    }

    @Test
    public void adhocRequest_factoryMethod_returnsTypeAdhocRequest() {
        AuditLogItem item = AuditLogItem.adhocRequest("R-001", "Sara Khan", "uid2", "Guard Bilal", 3_000L, "Walk-in", "Pending");
        assertEquals(AuditLogItem.TYPE_ADHOC_REQUEST, item.getType());
    }

    @Test
    public void emergency_factoryMethod_returnsTypeEmergency() {
        AuditLogItem item = AuditLogItem.emergency("E-001", "Fire", "uid3", "Guard Usman", 4_000L, "Gate fire");
        assertEquals(AuditLogItem.TYPE_EMERGENCY, item.getType());
    }

    @Test public void entry_typeLabel_isEntry() {
        assertEquals("Entry", AuditLogItem.entry("P-1","V",null,null,0,null).getTypeLabel());
    }
    @Test public void exit_typeLabel_isExit() {
        assertEquals("Exit", AuditLogItem.exit("P-1","V",null,null,0,null).getTypeLabel());
    }
    @Test public void adhocRequest_typeLabel_isAdHoc() {
        assertEquals("Ad-hoc", AuditLogItem.adhocRequest("R-1","V",null,null,0,null,"Pending").getTypeLabel());
    }
    @Test public void emergency_typeLabel_isEmergency() {
        assertEquals("Emergency", AuditLogItem.emergency("E-1","Fire",null,null,0,null).getTypeLabel());
    }


    @Test
    public void entry_fields_areStoredCorrectly() {
        AuditLogItem item = AuditLogItem.entry("P-042", "Omar", "uid99", "Guard X", 5_000L, "Delivery");
        assertEquals("P-042",    item.getEventId());
        assertEquals("Omar",     item.getVisitorOrType());
        assertEquals("uid99",    item.getActorId());
        assertEquals("Guard X",  item.getActorName());
        assertEquals(5_000L,     item.getEventTimeMillis());
        assertEquals("Delivery", item.getExtraDetail());
    }

    @Test
    public void adhocRequest_detail_includesStatusAndReason() {
        AuditLogItem item = AuditLogItem.adhocRequest("R-10","Hina",null,null,0,"Delivery","Pending");
        assertTrue(item.getExtraDetail().contains("Delivery"));
        assertTrue(item.getExtraDetail().contains("Pending"));
    }

    @Test
    public void adhocRequest_nullReason_detailContainsStatusOnly() {
        AuditLogItem item = AuditLogItem.adhocRequest("R-11","Hina",null,null,0,null,"Approved");
        assertEquals("Status: Approved", item.getExtraDetail());
    }

    @Test
    public void adhocRequest_emptyReason_detailContainsStatusOnly() {
        AuditLogItem item = AuditLogItem.adhocRequest("R-12","Hina",null,null,0,"   ","Rejected");
        assertEquals("Status: Rejected", item.getExtraDetail());
    }

    private List<AuditLogItem> applyFilters(List<AuditLogItem> all, Integer typeFilter,
                                            long fromMs, long toMs, String query) {
        List<AuditLogItem> result = new ArrayList<>();
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ENGLISH);
        for (AuditLogItem item : all) {
            if (typeFilter != null && item.getType() != typeFilter) continue;
            if (fromMs > 0 && item.getEventTimeMillis() < fromMs) continue;
            if (toMs   > 0 && item.getEventTimeMillis() > toMs)   continue;
            if (!q.isEmpty()) {
                boolean match = contains(item.getEventId(), q)
                        || contains(item.getVisitorOrType(), q)
                        || contains(item.getActorName(), q)
                        || contains(item.getActorId(), q)
                        || contains(item.getExtraDetail(), q)
                        || contains(item.getTypeLabel(), q);
                if (!match) continue;
            }
            result.add(item);
        }
        return result;
    }

    private boolean contains(String s, String q) {
        return s != null && s.toLowerCase(Locale.ENGLISH).contains(q);
    }

    private List<AuditLogItem> mixedList() {
        List<AuditLogItem> list = new ArrayList<>();
        list.add(AuditLogItem.entry("P-1", "Alice", null, null, 1000, "Meeting"));
        list.add(AuditLogItem.exit("P-1", "Alice", null, null, 2000, "Meeting"));
        list.add(AuditLogItem.adhocRequest("R-1", "Bob", null, null, 3000, "Walk-in", "Pending"));
        list.add(AuditLogItem.emergency("E-1", "Fire", null, null, 4000, "Gate fire"));
        return list;
    }

    @Test public void typeFilter_null_returnsAllItems() {
        assertEquals(4, applyFilters(mixedList(), null, 0, 0, "").size());
    }
    @Test public void typeFilter_entry_returnsOnlyEntries() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_ENTRY, 0, 0, "");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_ENTRY, r.get(0).getType());
    }
    @Test public void typeFilter_exit_returnsOnlyExits() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_EXIT, 0, 0, "");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_EXIT, r.get(0).getType());
    }
    @Test public void typeFilter_adhoc_returnsOnlyAdhocRequests() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_ADHOC_REQUEST, 0, 0, "");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_ADHOC_REQUEST, r.get(0).getType());
    }
    @Test public void typeFilter_emergency_returnsOnlyEmergencies() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_EMERGENCY, 0, 0, "");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_EMERGENCY, r.get(0).getType());
    }
    @Test public void typeFilter_entry_emptyList_returnsEmptyList() {
        assertEquals(0, applyFilters(new ArrayList<>(), AuditLogItem.TYPE_ENTRY, 0, 0, "").size());
    }
    @Test public void dateRange_fromOnly_excludesEarlierItems() {
        List<AuditLogItem> r = applyFilters(mixedList(), null, 2500, 0, "");
        assertEquals(2, r.size());
        for (AuditLogItem item : r) assertTrue(item.getEventTimeMillis() >= 2500);
    }
    @Test public void dateRange_toOnly_excludesLaterItems() {
        List<AuditLogItem> r = applyFilters(mixedList(), null, 0, 2500, "");
        assertEquals(2, r.size());
        for (AuditLogItem item : r) assertTrue(item.getEventTimeMillis() <= 2500);
    }
    @Test public void dateRange_fromAndTo_returnsItemsInWindow() {
        assertEquals(2, applyFilters(mixedList(), null, 1500, 3500, "").size());
    }
    @Test public void dateRange_noItemsInRange_returnsEmpty() {
        assertTrue(applyFilters(mixedList(), null, 5000, 9000, "").isEmpty());
    }
    @Test public void dateRange_exactBoundary_isInclusive() {
        List<AuditLogItem> r = applyFilters(mixedList(), null, 2000, 2000, "");
        assertEquals(1, r.size());
        assertEquals(2000, r.get(0).getEventTimeMillis());
    }
    @Test public void search_visitorName_matchesCorrectItem() {
        assertEquals(2, applyFilters(mixedList(), null, 0, 0, "Alice").size());
    }
    @Test public void search_eventId_matchesCorrectItem() {
        List<AuditLogItem> r = applyFilters(mixedList(), null, 0, 0, "E-1");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_EMERGENCY, r.get(0).getType());
    }
    @Test public void search_caseInsensitive() {
        assertEquals(2, applyFilters(mixedList(), null, 0, 0, "alice").size());
    }
    @Test public void search_noMatch_returnsEmpty() {
        assertTrue(applyFilters(mixedList(), null, 0, 0, "xyz_no_match").isEmpty());
    }
    @Test public void search_typeLabel_matchesEmergencyItems() {
        List<AuditLogItem> r = applyFilters(mixedList(), null, 0, 0, "emergency");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_EMERGENCY, r.get(0).getType());
    }
    @Test public void search_typeLabel_adhoc_matchesAdhocItems() {
        List<AuditLogItem> r = applyFilters(mixedList(), null, 0, 0, "ad-hoc");
        assertEquals(1, r.size());
        assertEquals(AuditLogItem.TYPE_ADHOC_REQUEST, r.get(0).getType());
    }
    @Test public void search_emptyString_returnsAll() {
        assertEquals(4, applyFilters(mixedList(), null, 0, 0, "").size());
    }
    @Test public void search_whitespaceOnly_returnsAll() {
        assertEquals(4, applyFilters(mixedList(), null, 0, 0, "   ").size());
    }
    @Test public void combined_typeAndSearch_narrowsResults() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_ENTRY, 0, 0, "alice");
        assertEquals(1, r.size());
    }
    @Test public void combined_typeAndDateRange_narrowsResults() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_EXIT, 1500, 0, "");
        assertEquals(1, r.size());
        assertEquals(2000, r.get(0).getEventTimeMillis());
    }
    @Test public void combined_allFilters_returnsCorrectSubset() {
        List<AuditLogItem> r = applyFilters(mixedList(), AuditLogItem.TYPE_ADHOC_REQUEST, 2500, 4000, "bob");
        assertEquals(1, r.size());
        assertEquals("Bob", r.get(0).getVisitorOrType());
    }
    @Test public void nullActorName_doesNotCrash() {
        AuditLogItem item = AuditLogItem.entry("P-99", "Visitor", null, null, 1000, null);
        List<AuditLogItem> list = new ArrayList<>();
        list.add(item);
        assertTrue(applyFilters(list, null, 0, 0, "someactor").isEmpty());
    }
    @Test public void nullExtraDetail_doesNotCrash() {
        AuditLogItem item = AuditLogItem.emergency("E-99","Medical","uid5","Guard Z",9000,null);
        List<AuditLogItem> list = new ArrayList<>();
        list.add(item);
        assertTrue(applyFilters(list, null, 0, 0, "somedetail").isEmpty());
    }
    @Test public void zeroTimestamp_passesDateFilter_whenNoRangeSet() {
        AuditLogItem item = AuditLogItem.entry("P-0","V",null,null,0L,null);
        List<AuditLogItem> list = new ArrayList<>();
        list.add(item);
        assertEquals(1, applyFilters(list, null, 0, 0, "").size());
    }
}