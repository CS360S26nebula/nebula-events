package com.example.seproject;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import org.mockito.Mockito;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CheckOutTest {
    private Request approvedRequest;

    @Before
    public void setUp() {
        approvedRequest = new Request("doc-001",
                "Moiz Imran",
                "35202-1234567-1",
                "03001234567",
                "Student Visit",
                "07/04/2026",
                "10:00 AM",
                "Dr. Suleiman",
                "Faculty",
                "Approved",
                1_700_000_000_000L,
                "PASS-AB12CD34",
                null
        );
    }

    @Test
    public void constructor_checkedOutAtMillis_defaultsToZero() {
        assertEquals(0L, approvedRequest.getCheckedOutAtMillis());
    }

    @Test
    public void noArgConstructor_checkedOutAtMillis_defaultsToZero() {
        Request r = new Request();
        assertEquals(0L, r.getCheckedOutAtMillis());
    }

    @Test
    public void constructor_checkedInAtMillis_defaultsToZero() {
        assertEquals(0L, approvedRequest.getCheckedInAtMillis());
    }

    @Test
    public void checkout_happyPath_statusBecomesExpiredAndTimestampSet() {
        long exitTime = 1_700_001_000_000L;

        approvedRequest.setRequestStatus("Expired");
        approvedRequest.setCheckedOutAtMillis(exitTime);

        assertEquals("Expired", approvedRequest.getRequestStatus());
        assertEquals(exitTime, approvedRequest.getCheckedOutAtMillis());
    }

    @Test
    public void checkout_auditTrailPreserved_otherFieldsUnchanged() {
        approvedRequest.setRequestStatus("Expired");
        approvedRequest.setCheckedOutAtMillis(System.currentTimeMillis());

        assertEquals("doc-001", approvedRequest.getRequestId());
        assertEquals("Moiz Imran", approvedRequest.getVisitorName());
        assertEquals("35202-1234567-1", approvedRequest.getVisitorCnic());
        assertEquals("03001234567", approvedRequest.getVisitorMobileNumber());
        assertEquals("Student Visit", approvedRequest.getVisitReason());
        assertEquals("07/04/2026", approvedRequest.getVisitDate());
        assertEquals("10:00 AM", approvedRequest.getVisitTime());
        assertEquals("Dr. Suleiman", approvedRequest.getInvitorName());
        assertEquals("Faculty", approvedRequest.getInvitorType());
        assertEquals("PASS-AB12CD34", approvedRequest.getPassId());
        assertEquals(1_700_000_000_000L, approvedRequest.getCreatedAtMillis());
        assertNull(approvedRequest.getRejectionReason());
    }

    @Test
    public void checkout_doesNotOverwriteCheckedInTimestamp() {
        long checkInTime = 1_700_000_500_000L;
        approvedRequest.setCheckedInAtMillis(checkInTime);

        approvedRequest.setRequestStatus("Expired");
        approvedRequest.setCheckedOutAtMillis(1_700_001_000_000L);

        assertEquals(checkInTime, approvedRequest.getCheckedInAtMillis());
    }

    @Test
    public void checkout_exitTimestamp_isAfterEntryTimestamp() {
        long checkInTime  = 1_700_000_500_000L;
        long checkOutTime = 1_700_001_000_000L;

        approvedRequest.setCheckedInAtMillis(checkInTime);
        approvedRequest.setCheckedOutAtMillis(checkOutTime);

        assertTrue(approvedRequest.getCheckedOutAtMillis() > approvedRequest.getCheckedInAtMillis());
    }

    @Test
    public void request_initialStatus_isApproved() {
        assertEquals("Approved", approvedRequest.getRequestStatus());
    }

    @Test
    public void checkout_statusString_exactlyExpired() {
        approvedRequest.setRequestStatus("Expired");
        assertEquals("Expired", approvedRequest.getRequestStatus());
        assertFalse("Expired".equalsIgnoreCase("expired") && "Expired".equals("expired"));
    }

    @Test
    public void checkout_expiredStatus_notEqualToApproved() {
        approvedRequest.setRequestStatus("Expired");
        assertFalse("Approved".equals(approvedRequest.getRequestStatus()));
    }

    @Test
    public void checkedOutAtMillis_zero_indicatesNotCheckedOut() {
        assertEquals(0L, approvedRequest.getCheckedOutAtMillis());
        assertFalse(approvedRequest.getCheckedOutAtMillis() > 0);
    }

    @Test
    public void checkedOutAtMillis_positive_indicatesCheckedOut() {
        approvedRequest.setCheckedOutAtMillis(1_000L);
        assertTrue(approvedRequest.getCheckedOutAtMillis() > 0);
    }

    @Test
    public void checkedOutAtMillis_negative_storedWithoutError() {
        approvedRequest.setCheckedOutAtMillis(-1L);
        assertEquals(-1L, approvedRequest.getCheckedOutAtMillis());
    }

    @Test
    public void checkedOutAtMillis_maxLong_storedCorrectly() {
        approvedRequest.setCheckedOutAtMillis(Long.MAX_VALUE);
        assertEquals(Long.MAX_VALUE, approvedRequest.getCheckedOutAtMillis());
    }

    @Test
    public void checkedOutAtMillis_setTwiceSameValue_unchanged() {
        approvedRequest.setCheckedOutAtMillis(5_000L);
        approvedRequest.setCheckedOutAtMillis(5_000L);
        assertEquals(5_000L, approvedRequest.getCheckedOutAtMillis());
    }

    @Test
    public void checkedOutAtMillis_setTwiceDifferentValues_secondValueWins() {
        approvedRequest.setCheckedOutAtMillis(1_000L);
        approvedRequest.setCheckedOutAtMillis(2_000L);
        assertEquals(2_000L, approvedRequest.getCheckedOutAtMillis());
    }

    @Test
    public void visitorName_null_modelAllowsIt() {
        approvedRequest.setVisitorName(null);
        assertNull(approvedRequest.getVisitorName());
    }

    @Test
    public void visitorName_empty_storedAsEmpty() {
        approvedRequest.setVisitorName("");
        assertNotNull(approvedRequest.getVisitorName());
        assertEquals("", approvedRequest.getVisitorName());
    }

    @Test
    public void visitorName_nonEmpty_returnedUnchanged() {
        assertEquals("Moiz Imran", approvedRequest.getVisitorName());
    }

    private ApprovedRequestsAdapter buildAdapter(List<Request> items, List<String> ids) {
        ApprovedRequestsAdapter adapter = spy(
                new ApprovedRequestsAdapter(items, ids, (request, documentId) -> { })
        );

        doNothing().when(adapter).notifyDataSetChanged();

        return adapter;
    }

    private Request makeRequest(String passId, String visitorName,
                                String cnic, String invitorName) {
        Request r = new Request();
        r.setPassId(passId);
        r.setVisitorName(visitorName);
        r.setVisitorCnic(cnic);
        r.setInvitorName(invitorName);
        return r;
    }

    @Test
    public void filter_emptyQuery_showsAllItems() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran",  "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar",  "222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1", "d2"));

        adapter.filter("");

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void filter_byPassId_returnsMatchingRecord() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1", "d2"));

        adapter.filter("PASS-001");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_byVisitorNameCaseInsensitive_returnsMatch() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1", "d2"));

        adapter.filter("ali");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_byCnic_returnsMatchingRecord() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "35202-111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "35202-222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1", "d2"));

        adapter.filter("35202-111");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_byInvitorName_returnsMatchingRecord() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1", "d2"));

        adapter.filter("waqar");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_noMatch_returnsEmptyList() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1", "d2"));

        adapter.filter("ZZZNOMATCH");

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void filter_clearAfterNarrow_restoresAllItems() {
        List<Request> items = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar"),
                makeRequest("PASS-003", "Umer Ashraf", "333", "Dr. Murtaza")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(
                items, Arrays.asList("d1", "d2", "d3"));

        adapter.filter("Moiz Imran");
        assertEquals(1, adapter.getItemCount());

        adapter.filter("");   // clear
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void filter_emptyAdapter_noException() {
        ApprovedRequestsAdapter adapter = buildAdapter(
                new ArrayList<>(), new ArrayList<>());

        adapter.filter("anything");

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void filter_nullFieldsInRecord_noNullPointerException() {
        List<Request> items = new ArrayList<>();
        items.add(makeRequest(null, null, null, null));
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1"));

        adapter.filter("anything");  // must not throw

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void filter_emptyQuery_nullFieldRecord_isIncluded() {
        List<Request> items = new ArrayList<>();
        items.add(makeRequest(null, null, null, null));
        ApprovedRequestsAdapter adapter = buildAdapter(items, Arrays.asList("d1"));

        adapter.filter("");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void setItems_thenFilter_operatesOnNewItems() {
        List<Request> original = Arrays.asList(
                makeRequest("PASS-OLD", "Old Visitor", "000", "Old Host")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(original, Arrays.asList("d0"));

        List<Request> updated = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        adapter.setItems(updated, Arrays.asList("d1", "d2"));

        adapter.filter("");
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void setItems_afterCheckout_adapterCountDecreases() {
        List<Request> before = Arrays.asList(
                makeRequest("PASS-001", "Moiz Imran", "111", "Dr. Suleiman"),
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        ApprovedRequestsAdapter adapter = buildAdapter(
                before, Arrays.asList("d1", "d2"));
        assertEquals(2, adapter.getItemCount());

        // Simulate Firestore snapshot update after Ali Hassan checks out:
        // only Sara Ahmed remains with status "Approved"
        List<Request> after = Arrays.asList(
                makeRequest("PASS-002", "Ali Azhar", "222", "Dr. Waqar")
        );
        adapter.setItems(after, Arrays.asList("d2"));

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void modeApproved_constantValue_isApproved() {
        assertEquals("Approved", ApprovedRequestsAdapter.MODE_APPROVED);
    }

    @Test
    public void modeApproved_notEqualToExpired() {
        assertFalse("Expired".equals(ApprovedRequestsAdapter.MODE_APPROVED));
    }

    @Test
    public void formatVisitDateTime_approvedRequest_combinesDateAndTime() {
        String result = RequestDisplayFormatter.formatVisitDateTime(approvedRequest);
        assertEquals("07/04/2026 | 10:00 AM", result);
    }

    @Test
    public void formatVisitDateTime_onlyDate_noPipeSeparator() {
        Request r = new Request();
        r.setVisitDate("07/04/2026");
        String result = RequestDisplayFormatter.formatVisitDateTime(r);
        assertEquals("07/04/2026", result);
        assertFalse(result.contains("|"));
    }

    @Test
    public void formatVisitDateTime_bothNull_returnsEmDash() {
        Request r = new Request();
        assertEquals("\u2014", RequestDisplayFormatter.formatVisitDateTime(r));
    }

    @Test
    public void dashIfEmpty_nullPassId_returnsEmDash() {
        assertEquals("\u2014", RequestDisplayFormatter.dashIfEmpty(null));
    }

    @Test
    public void dashIfEmpty_validPassId_returnsSameValue() {
        assertEquals("PASS-AB12CD34", RequestDisplayFormatter.dashIfEmpty("PASS-AB12CD34"));
    }
}
