package testcases;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import coreUtilities.utils.FileOperations;
import rest.ApiUtil;
import rest.CustomResponse;

public class RestAssured_TestCases_PL1 {

	FileOperations fileOperations = new FileOperations();
	private final String EXCEL_FILE_PATH = "src/main/resources/config.xlsx"; // Path to the Excel file
	private final String FILEPATH = "src/main/java/rest/ApiUtil.java";
	ApiUtil apiUtil;

	@Test(priority = 1, groups = { "PL1" }, description = "1. Send a GET request to get list of stocks\n"
			+ "2. Validate that all the ItemId, ItemName, and GenericName are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getAllStocksTest() throws IOException {
		apiUtil = new ApiUtil();

		// Send GET request
		CustomResponse customResponse = apiUtil.getAllStocks("/PharmacyStock/AllStockDetails", null);

		// Validate the implementation of getAllStocks
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAllStocks",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getAllStocks must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAllStocks", customResponse),
				"Must have all required fields in the response.");

		// Validate the status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate top-level status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate that ItemId, ItemName, and GenericName are not null
		List<Object> itemIds = customResponse.getItemIds();
		List<Object> itemNames = customResponse.getItemNames();
		List<Object> genericNames = customResponse.getGenericNames();

		Assert.assertFalse(itemIds.isEmpty(), "ItemId list should not be empty.");
		Assert.assertFalse(itemNames.isEmpty(), "ItemName list should not be empty.");
		Assert.assertFalse(genericNames.isEmpty(), "GenericName list should not be empty.");

		// Validate that none of the fields are null for each entry
		for (int i = 0; i < itemIds.size(); i++) {
			Assert.assertNotNull(itemIds.get(i), "ItemId at index " + i + " should not be null.");
			Assert.assertNotNull(itemNames.get(i), "ItemName at index " + i + " should not be null.");
			Assert.assertNotNull(genericNames.get(i), "GenericName at index " + i + " should not be null.");
		}

		// Print response for debugging
		System.out.println("All Stocks Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 2, groups = { "PL1" }, description = "1. Send a GET request to get details of main store\n"
			+ "2. Validate that all StoreId are not null.\n" + "3. Verify the response status code is 200.")
	public void getMainStoreTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getMainStore("/PharmacySettings/MainStore", null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getMainStore",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getMainStore must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getMainStore", customResponse),
				"Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate StoreId, Category, and IsActive fields are not null
		Assert.assertNotNull(customResponse.getStoreId(), "StoreId should not be null.");
		Assert.assertNotNull(customResponse.getCategory(), "Category should not be null.");
		Assert.assertNotNull(customResponse.getIsActive(), "IsActive should not be null.");

		// Print the response
		System.out.println("Main Store Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 3, groups = {
			"PL1" }, description = "1. Send a GET request to get requisition list by date range\n"
					+ "2. Validate that RequisitionNo, RequisitionStatus are not null and requisitionIds are unique.\n"
					+ "3. Verify the response status code is 200.")
	public void getRequisitionByDateRangeTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-19";

		CustomResponse customResponse = apiUtil.getRequisitionByDateRange(
				"/DispensaryRequisition/Dispensary/1?FromDate=" + fromDate + "&ToDate=" + toDate, null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getRequisitionByDateRange", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getRequisitionByDateRange must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getRequisitionByDateRange", customResponse),
				"Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate requisition list fields
		List<Object> requisitionNos = customResponse.getItemIds();
		List<Object> requisitionStatuses = customResponse.getItemNames();
		List<Object> requisitionIds = customResponse.getGenericNames();

		// Validate that requisition fields are not null
		Assert.assertFalse(requisitionNos.isEmpty(), "RequisitionNos list should not be empty.");
		Assert.assertFalse(requisitionStatuses.isEmpty(), "RequisitionStatuses list should not be empty.");
		Assert.assertFalse(requisitionIds.isEmpty(), "RequisitionIds list should not be empty.");

		// Validate that requisition IDs are unique
		Set<Object> uniqueRequisitionIds = new HashSet<>(requisitionIds);
		Assert.assertEquals(uniqueRequisitionIds.size(), requisitionIds.size(), "Requisition IDs should be unique.");

		// Validate that RequisitionNo and RequisitionStatus are not null
		for (int i = 0; i < requisitionNos.size(); i++) {
			Assert.assertNotNull(requisitionNos.get(i), "RequisitionNo at index " + i + " should not be null.");
			Assert.assertNotNull(requisitionStatuses.get(i),
					"RequisitionStatus at index " + i + " should not be null.");
		}

		// Print the response
		System.out.println("Requisition Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 4, groups = { "PL1" }, description = "1. Send a GET request to get patient consumptions list\n"
			+ "2. Validate that all PatientId, HospitalNo, and PatientVisitId are not null.\n"
			+ "3. Verify the response status code is 200.")
	public void getPatientConsumptionsTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getPatientConsumptions("/PatientConsumption/PatientConsumptions", null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getPatientConsumptions", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getPatientConsumptions must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getPatientConsumptions", customResponse),
				"Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate PatientId, HospitalNo, and PatientVisitId fields are not null
		List<Object> patientIds = customResponse.getItemIds();
		List<Object> hospitalNos = customResponse.getItemNames();
		List<Object> patientVisitIds = customResponse.getGenericNames();

		// Validate that none of the fields are null
		Assert.assertFalse(patientIds.isEmpty(), "PatientIds list should not be empty.");
		Assert.assertFalse(hospitalNos.isEmpty(), "HospitalNos list should not be empty.");
		Assert.assertFalse(patientVisitIds.isEmpty(), "PatientVisitIds list should not be empty.");

		// Validate that PatientId, HospitalNo, and PatientVisitId are not null for each
		// patient
		for (int i = 0; i < patientIds.size(); i++) {
			Assert.assertNotNull(patientIds.get(i), "PatientId at index " + i + " should not be null.");
			Assert.assertNotNull(hospitalNos.get(i), "HospitalNo at index " + i + " should not be null.");
			Assert.assertNotNull(patientVisitIds.get(i), "PatientVisitId at index " + i + " should not be null.");
		}

		// Print the response
		System.out.println("Patient Consumptions Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 5, groups = {
			"PL1" }, description = "1. Send a GET request to get patient consumption information\n"
					+ "2. Validate that PatientName, HospitalNo, and StoreId are not null.\n"
					+ "3. Verify the response status code is 200.")
	public void getPatientConsumptionInfoTest() throws IOException {
		apiUtil = new ApiUtil();

		String endpoint = "/PatientConsumption/PatientConsumptionInfo?PatientId=114&patientVisitId=53";

		CustomResponse customResponse = apiUtil.getPatientConsumptionInfoByPatientIdAndVisitId(endpoint, null);

		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getPatientConsumptionInfoByPatientIdAndVisitId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getPatientConsumptionInfoByPatientIdAndVisitId must be implemented using Rest Assured methods only.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getPatientConsumptionInfoByPatientIdAndVisitId",
				customResponse), "Must have all required fields in the response.");

		// Validate HTTP status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");

		// Validate the status field
		String status = customResponse.getStatus();
		Assert.assertEquals(status, "OK", "Status should be OK.");

		// Validate PatientName, HospitalNo, and StoreId fields are not null
		Assert.assertNotNull(customResponse.getStoreId(), "PatientName should not be null.");
		Assert.assertNotNull(customResponse.getCategory(), "HospitalNo should not be null.");
		Assert.assertNotNull(customResponse.getIsActive(), "StoreId should not be null.");

		// Print the response
		System.out.println("Patient Consumption Info Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 6, groups = { "PL1" }, description = "Retrieve and validate Billing Scheme By Scheme ID.")
	public void getBillingSchemeBySchemeIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String schemeId = "4";

		CustomResponse customResponse = apiUtil
				.getBillingSchemeBySchemeId("/PatientConsumption/PharmacyIpBillingScheme?schemeId=" + schemeId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getBillingSchemeBySchemeId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getBillingSchemeBySchemeId method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getBillingSchemeBySchemeId", customResponse),
				"Must have all required fields in the response.");

		// Validate response
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate the extracted fields
		Assert.assertNotNull(customResponse.getStoreId(), "SchemeCode should not be null.");
		Assert.assertNotNull(customResponse.getCategory(), "SchemeName should not be null.");
		Assert.assertEquals(String.valueOf(customResponse.getIsActive()), schemeId,
				"SchemeId should match the requested value.");

		System.out.println("Billing Scheme Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 7, groups = { "PL1" }, description = "Retrieve and validate Billing Summary By Patient ID.")
	public void getBillingSummaryByPatientIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientId = "114";

		CustomResponse customResponse = apiUtil
				.getBillingSummaryByPatientId("/PharmacySales/PatientBillingSummary?patientId=" + patientId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getBillingSummaryByPatientId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getBillingSummaryByPatientId method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getBillingSummaryByPatientId", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate the extracted fields
		Assert.assertEquals(String.valueOf(customResponse.getPatientId()), patientId,
				"PatientId should match the requested value.");
		Assert.assertNotNull(customResponse.getTotalDue(), "TotalDue should not be null.");

		System.out.println("Billing Summary Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 8, groups = {
			"PL1" }, description = "Retrieve and validate Patient Consumptions List By Patient ID.")
	public void getConsumptionsListOfAPatientByIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientId = "114";
		String patientVisitId = "53";

		CustomResponse customResponse = apiUtil
				.getConsumptionsListOfAPatientById("/PatientConsumption/ConsumptionsOfPatient?patientId=" + patientId
						+ "&patientVisitId=" + patientVisitId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getConsumptionsListOfAPatientById", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getConsumptionsListOfAPatientById method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getConsumptionsListOfAPatientById", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate the extracted fields
		Assert.assertFalse(customResponse.getItemIds().isEmpty(), "PatientConsumptionIds list should not be empty.");
		Assert.assertFalse(customResponse.getItemNames().isEmpty(), "ConsumptionReceiptNos list should not be empty.");
		Assert.assertFalse(customResponse.getGenericNames().isEmpty(), "TotalAmounts list should not be empty.");

		// Check for unique PatientConsumptionIds
		Set<Object> uniqueIds = new HashSet<>(customResponse.getItemIds());
		Assert.assertEquals(uniqueIds.size(), customResponse.getItemIds().size(),
				"PatientConsumptionId values should be unique.");

		// Print the response for debugging
		System.out.println("Patient Consumptions List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 9, groups = { "PL1" }, description = "Retrieve and validate the return consumptions list.")
	public void getReturnConsumptionsListTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getReturnConsumptionsList("/PatientConsumption/Returns", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getReturnConsumptionsList", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getReturnConsumptionsList method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getReturnConsumptionsList", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate unique ConsumptionReturnReceiptNo and non-null PatientId
		List<Object> consumptionReturnReceiptNos = customResponse.getItemIds();
		List<Object> hospitalNos = customResponse.getItemNames();
		List<Object> patientIds = customResponse.getGenericNames();

		Set<Object> uniqueReceiptNos = new HashSet<>();
		for (int i = 0; i < consumptionReturnReceiptNos.size(); i++) {
			Assert.assertNotNull(consumptionReturnReceiptNos.get(i), "ConsumptionReturnReceiptNo should not be null.");
			Assert.assertNotNull(patientIds.get(i), "PatientId should not be null.");
			uniqueReceiptNos.add(consumptionReturnReceiptNos.get(i));
		}
		Assert.assertEquals(uniqueReceiptNos.size(), consumptionReturnReceiptNos.size(),
				"ConsumptionReturnReceiptNo values should be unique.");

		System.out.println("Return Consumptions List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 10, groups = { "PL1" }, description = "Retrieve and validate the list of discharged patients.")
	public void getDischargedPatientsTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-19";

		CustomResponse customResponse = apiUtil.getDischargedPatients(
				"/Admission/DischargedPatients?admissionStatus=discharged&FromDate=" + fromDate + "&ToDate=" + toDate,
				null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getDischargedPatients",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getDischargedPatients method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getDischargedPatients", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate unique PatientVisitId and non-null PatientId
		List<Object> visitCodes = customResponse.getItemIds();
		List<Object> patientVisitIds = customResponse.getItemNames();
		List<Object> patientIds = customResponse.getGenericNames();

		Set<Object> uniqueVisitIds = new HashSet<>();
		Set<Object> uniquePatientIds = new HashSet<>();

		for (int i = 0; i < patientVisitIds.size(); i++) {
			Assert.assertNotNull(patientVisitIds.get(i), "PatientVisitId should not be null.");
			Assert.assertNotNull(patientIds.get(i), "PatientId should not be null.");
			uniqueVisitIds.add(patientVisitIds.get(i));
			uniquePatientIds.add(patientIds.get(i));
		}

		Assert.assertEquals(uniqueVisitIds.size(), patientVisitIds.size(), "PatientVisitId values should be unique.");
		Assert.assertEquals(uniquePatientIds.size(), patientIds.size(), "PatientId values should be unique.");

		System.out.println("Discharged Patients Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 11, groups = { "PL1" }, description = "Retrieve and validate the list of admitted patients.")
	public void getAdmittedPatientsTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-19";

		CustomResponse customResponse = apiUtil.getAdmittedPatients(
				"/IpBilling/AdmittedPatients?admissionStatus=admitted&FromDate=" + fromDate + "&ToDate=" + toDate,
				null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getAdmittedPatients",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getAdmittedPatients method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getAdmittedPatients", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate unique PatientId and VisitId, and non-null DischargeDate should be
		// null for admitted patients
		List<Object> patientIds = customResponse.getItemIds();
		List<Object> visitIds = customResponse.getItemNames();
		List<Object> dischargeDates = customResponse.getGenericNames();

		Set<Object> uniquePatientIds = new HashSet<>();
		Set<Object> uniqueVisitIds = new HashSet<>();

		for (int i = 0; i < patientIds.size(); i++) {
			Assert.assertNotNull(patientIds.get(i), "PatientId should not be null.");
			Assert.assertNotNull(visitIds.get(i), "VisitId should not be null.");
			Assert.assertNull(dischargeDates.get(i), "DischargeDate should be null for admitted patients.");

			uniquePatientIds.add(patientIds.get(i));
			uniqueVisitIds.add(visitIds.get(i));
		}

		Assert.assertEquals(uniquePatientIds.size(), patientIds.size(), "PatientId values should be unique.");
		Assert.assertEquals(uniqueVisitIds.size(), visitIds.size(), "VisitId values should be unique.");

		System.out.println("Admitted Patients Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 12, groups = { "PL1" }, description = "Retrieve and validate IPD patients by patient name.")
	public void searchIpdPatientByPatientIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientName = "Devid8 Roy8";

		CustomResponse customResponse = apiUtil
				.searchIpdPatientByPatientId("/Patient/IPDPatientSearch?search=" + patientName, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"searchIpdPatientByPatientId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "searchIpdPatientByPatientId method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("searchIpdPatientByPatientId", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate non-null PatientId and PatientCode
		List<Object> patientIds = customResponse.getPatientIds();
		List<Object> patientCodes = customResponse.getPatientCodes();

		Set<Object> uniquePatientIds = new HashSet<>();
		Set<Object> uniquePatientCodes = new HashSet<>();

		for (int i = 0; i < patientIds.size(); i++) {
			Assert.assertNotNull(patientIds.get(i), "PatientId should not be null.");
			Assert.assertNotNull(patientCodes.get(i), "PatientCode should not be null.");

			uniquePatientIds.add(patientIds.get(i));
			uniquePatientCodes.add(patientCodes.get(i));
		}

		// Check uniqueness of PatientId and PatientCode
		Assert.assertEquals(uniquePatientIds.size(), patientIds.size(), "PatientId values should be unique.");
		Assert.assertEquals(uniquePatientCodes.size(), patientCodes.size(), "PatientCode values should be unique.");

		System.out.println("IPD Patients Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 13, groups = { "PL1" }, description = "Retrieve and validate patients' provisional information.")
	public void getPatientProvisionalInfoTest() throws IOException {
		apiUtil = new ApiUtil();

		CustomResponse customResponse = apiUtil.getPatientProvisionalInfo("/Billing/PatientsProvisionalInfo", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getPatientProvisionalInfo", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getPatientProvisionalInfo method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getPatientProvisionalInfo", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Validate non-null PatientId and PatientCode
		List<Object> patientIds = customResponse.getPatientIds();
		List<Object> patientCodes = customResponse.getPatientCodes();

		Set<Object> uniquePatientIds = new HashSet<>();
		Set<Object> uniquePatientCodes = new HashSet<>();

		for (int i = 0; i < patientIds.size(); i++) {
			Assert.assertNotNull(patientIds.get(i), "PatientId should not be null.");
			Assert.assertNotNull(patientCodes.get(i), "PatientCode should not be null.");

			uniquePatientIds.add(patientIds.get(i));
			uniquePatientCodes.add(patientCodes.get(i));
		}

		// Check uniqueness of PatientId and PatientCode
		Assert.assertEquals(uniquePatientIds.size(), patientIds.size(), "PatientId values should be unique.");
		Assert.assertEquals(uniquePatientCodes.size(), patientCodes.size(), "PatientCode values should be unique.");

		System.out.println("Provisional Information Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 14, groups = {
			"PL1" }, description = "Retrieve and validate provisional items list for a specific patient and scheme.")
	public void getProvisionalItemsListByPatientIdAndSchemeIdTest() throws IOException {
		apiUtil = new ApiUtil();
		String patientId = "188";
		String schemeId = "4";

		// API Call
		CustomResponse customResponse = apiUtil.getProvisionalItemsListByPatientIdAndSchemeId(
				"/Billing/ProvisionalItemsByPatientId?patientId=" + patientId + "&schemeId=" + schemeId, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getProvisionalItemsListByPatientIdAndSchemeId", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful,
				"getProvisionalItemsListByPatientIdAndSchemeId method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getProvisionalItemsListByPatientIdAndSchemeId",
				customResponse), "Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Map<String, Object>> provisionalItems = customResponse.getListResults();
		Assert.assertNotNull(provisionalItems, "ProvisionalItems list should not be null.");
		Assert.assertTrue(provisionalItems.size() > 0, "ProvisionalItems list should not be empty.");

		// Validate PatientId consistency in ProvisionalItems
		for (Map<String, Object> item : provisionalItems) {
			Assert.assertEquals(item.get("PatientId"), Integer.parseInt(patientId),
					"PatientId in ProvisionalItems should match the requested PatientId.");
		}

		// Print the response
		System.out.println("Provisional Items List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 15, groups = {
			"PL1" }, description = "Retrieve and validate billing invoices within a specific date range.")
	public void getInvoicesByDateRangeTest() throws IOException {
		apiUtil = new ApiUtil();
		String fromDate = "2020-01-01";
		String toDate = "2024-11-21";

		// API Call
		CustomResponse customResponse = apiUtil
				.getInvoicesByDateRange("/Billing/Invoices?FromDate=" + fromDate + "&ToDate=" + toDate, null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getInvoicesByDateRange", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getInvoicesByDateRange method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getInvoicesByDateRange", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Object> invoiceNumbers = customResponse.getPatientIds();
		List<Object> invoiceCodes = customResponse.getPatientCodes();

		Set<Object> uniqueInvoiceNumbers = new HashSet<>();
		Set<Object> uniqueInvoiceCodes = new HashSet<>();

		// Validate non-null InvoiceNumber and InvoiceCode
		for (int i = 0; i < invoiceNumbers.size(); i++) {
			Assert.assertNotNull(invoiceNumbers.get(i), "InvoiceNumber should not be null.");
			Assert.assertNotNull(invoiceCodes.get(i), "InvoiceCode should not be null.");

			uniqueInvoiceNumbers.add(invoiceNumbers.get(i));
			uniqueInvoiceCodes.add(invoiceCodes.get(i));
		}

		System.out.println("Billing Invoices Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 16, groups = { "PL1" }, description = "Retrieve and validate the list of providers.")
	public void getProviderListTest() throws IOException {
		apiUtil = new ApiUtil();

		// API Call
		CustomResponse customResponse = apiUtil.getProviderList("/Billing/GetProviderList", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getProviderList",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getProviderList method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getProviderList", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Object> employeeIds = customResponse.getPatientIds();
		List<Object> employeeNames = customResponse.getPatientCodes();

		Set<Object> uniqueEmployeeIds = new HashSet<>();
		Set<Object> uniqueEmployeeNames = new HashSet<>();

		// Validate non-null EmployeeId and EmployeeName
		for (int i = 0; i < employeeIds.size(); i++) {
			Assert.assertNotNull(employeeIds.get(i), "EmployeeId should not be null.");
			Assert.assertNotNull(employeeNames.get(i), "EmployeeName should not be null.");

			uniqueEmployeeIds.add(employeeIds.get(i));
			uniqueEmployeeNames.add(employeeNames.get(i));
		}

		// Check uniqueness of EmployeeId and EmployeeName
		Assert.assertEquals(uniqueEmployeeIds.size(), employeeIds.size(), "EmployeeId values should be unique.");
		Assert.assertEquals(uniqueEmployeeNames.size(), employeeNames.size(), "EmployeeName values should be unique.");

		System.out.println("Providers List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 17, groups = { "PL1" }, description = "Retrieve and validate the list of users.")
	public void getUsersListTest() throws IOException {
		apiUtil = new ApiUtil();

		// API Call
		CustomResponse customResponse = apiUtil.getUsersList("/Billing/ListUsers", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH, "getUsersList",
				List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getUsersList method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getUsersList", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		List<Object> userIds = customResponse.getItemIds();
		List<Object> shortNames = customResponse.getItemNames();
		List<Object> departmentNames = customResponse.getGenericNames();

		Set<Object> uniqueUserIds = new HashSet<>();
		Set<Object> uniqueShortNames = new HashSet<>();
		Set<Object> uniqueDepartmentNames = new HashSet<>();

		// Validate non-null UserId, ShortName, and DepartmentName
		for (int i = 0; i < userIds.size(); i++) {
			Assert.assertNotNull(userIds.get(i), "UserId should not be null.");
			Assert.assertNotNull(shortNames.get(i), "ShortName should not be null.");
			Assert.assertNotNull(departmentNames.get(i), "DepartmentName should not be null.");

			uniqueUserIds.add(userIds.get(i));
			uniqueShortNames.add(shortNames.get(i));
			uniqueDepartmentNames.add(departmentNames.get(i));
		}

		// Check uniqueness of UserId, ShortName, and DepartmentName
		Assert.assertEquals(uniqueUserIds.size(), userIds.size(), "UserId values should be unique.");
		Assert.assertEquals(uniqueShortNames.size(), shortNames.size(), "ShortName values should be unique.");

		System.out.println("Users List Response:");
		customResponse.getResponse().prettyPrint();
	}

	@Test(priority = 18, groups = { "PL1" }, description = "Retrieve and validate the current fiscal year details.")
	public void getCurrentFiscalYearDetailsTest() throws IOException {
		apiUtil = new ApiUtil();

		// API Call
		CustomResponse customResponse = apiUtil.getCurrentFiscalYearDetails("/Billing/CurrentFiscalYear", null);

		// Validate method implementation
		boolean isValidationSuccessful = TestCodeValidator.validateTestMethodFromFile(FILEPATH,
				"getCurrentFiscalYearDetails", List.of("given", "then", "extract", "response"));
		Assert.assertTrue(isValidationSuccessful, "getCurrentFiscalYearDetails method validation failed.");

		// Validate response structure
		Assert.assertTrue(TestCodeValidator.validateResponseFields("getCurrentFiscalYearDetails", customResponse),
				"Must have all required fields in the response.");

		// Validate response status code
		Assert.assertEquals(customResponse.getStatusCode(), 200, "Status code should be 200.");
		Assert.assertEquals(customResponse.getStatus(), "OK", "Status should be OK.");

		// Extract and validate results
		Object fiscalYearId = customResponse.getPatientId();
		Object fiscalYearName = customResponse.getTotalDue();

		// Validate FiscalYearId and FiscalYearName fields
		Assert.assertNotNull(fiscalYearId, "FiscalYearId should not be null.");
		Assert.assertNotNull(fiscalYearName, "FiscalYearName should not be null.");

		// Print the response
		System.out.println("Current Fiscal Year Response:");
		customResponse.getResponse().prettyPrint();
	}

}
