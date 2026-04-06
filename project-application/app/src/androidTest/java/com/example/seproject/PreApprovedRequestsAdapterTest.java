package com.example.seproject;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link PreApprovedRequestsAdapter}.
 *
 * @author Muhammad Yahya Ali
 * @version 1.0
 */
@RunWith(AndroidJUnit4.class)
public class PreApprovedRequestsAdapterTest {

    private List<Request> requests;
    private List<String> documentIds;

    @Before
    public void setUp() {
        requests = new ArrayList<>();
        documentIds = new ArrayList<>();

        Request r1 = new Request();
        r1.setPassId("PASS-1111");
        r1.setVisitorName("Ali Khan");
        r1.setVisitorCnic("35202-1111111-1");
        r1.setInvitorName("Dr Ahmed");

        Request r2 = new Request();
        r2.setPassId("PASS-2222");
        r2.setVisitorName("Sara Noor");
        r2.setVisitorCnic("35202-2222222-2");
        r2.setInvitorName("Prof Hamid");

        Request r3 = new Request();
        r3.setPassId("PASS-3333");
        r3.setVisitorName("Bilal Tariq");
        r3.setVisitorCnic("35202-3333333-3");
        r3.setInvitorName("Dr Ahmed");

        requests.add(r1);
        requests.add(r2);
        requests.add(r3);

        documentIds.add("doc1");
        documentIds.add("doc2");
        documentIds.add("doc3");
    }

    @Test
    public void filter_byVisitorName_returnsMatchingRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        adapter.filter("Sara");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_byPassId_returnsMatchingRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        adapter.filter("2222");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_byVisitorCnic_returnsMatchingRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        adapter.filter("3333333");

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void filter_byInvitorName_returnsMatchingRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        adapter.filter("Ahmed");

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void filter_emptyQuery_restoresAllRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        adapter.filter("Ali");
        assertEquals(1, adapter.getItemCount());

        adapter.filter("");
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void filter_noMatch_returnsZeroRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        adapter.filter("XYZ");

        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void approvedMode_filterStillWorksForApprovedList() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_APPROVED
        );

        adapter.filter("Ahmed");

        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void setItems_replacesExistingRows() {
        PreApprovedRequestsAdapter adapter = new PreApprovedRequestsAdapter(
                requests,
                documentIds,
                (request, documentId) -> { },
                PreApprovedRequestsAdapter.MODE_PRE_APPROVED
        );

        List<Request> newRequests = new ArrayList<>();
        List<String> newIds = new ArrayList<>();

        Request r4 = new Request();
        r4.setPassId("PASS-9999");
        r4.setVisitorName("Hina");
        r4.setVisitorCnic("35202-9999999-9");
        r4.setInvitorName("Ms Ayesha");

        newRequests.add(r4);
        newIds.add("doc9");

        adapter.setItems(newRequests, newIds);

        assertEquals(1, adapter.getItemCount());
    }
}