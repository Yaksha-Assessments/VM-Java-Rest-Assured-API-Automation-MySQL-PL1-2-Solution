package rest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ApiUtil {

	private static final String BASE_URL = "https://healthapp.yaksha.com/api";

	/**
	 * @Test1 This method retrieves and verifies the list of stocks.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes HTTP status code, status
	 *         message, and a list of stocks in the "Results" field, containing
	 *         details such as ItemID, ItemName, SalePrice, and CostPrice.
	 */
	public CustomResponse getAllStocks(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Add body if it's not null
		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extracting required data
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Directly extracting the lists of ItemId, ItemName, and GenericName
		List<Object> itemIds = response.jsonPath().getList("Results.ItemId");
		List<Object> itemNames = response.jsonPath().getList("Results.ItemName");
		List<Object> genericNames = response.jsonPath().getList("Results.GenericName");

		// Return a CustomResponse object
		return new CustomResponse(response, statusCode, status, itemIds, itemNames, genericNames);
	}

	/**
	 * @Test2 This method retrieves and verifies the details of the main store.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and details of the main store in the "Results" field,
	 *         containing details such as StoreId, ParentStoreId, Category, and
	 *         IsActive.
	 */
	public CustomResponse getMainStore(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Directly extracting the individual fields from "Results"
		Object storeId = response.jsonPath().get("Results.StoreId");
		Object category = response.jsonPath().get("Results.Category");
		Object isActive = response.jsonPath().get("Results.IsActive");

		return new CustomResponse(response, statusCode, status, storeId, category, isActive);
	}

	/**
	 * @Test3 This method retrieves and verifies the requisition list by date range.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and requisition list in the "Results" field,
	 *         containing details such as RequisitionId, RequisitionNo, and
	 *         RequisitionStatus.
	 */
	public CustomResponse getRequisitionByDateRange(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		List<Map<String, Object>> requisitionList = response.jsonPath().getList("Results.requisitionList");
		List<Object> requisitionNos = requisitionList.stream().map(req -> req.get("RequistionNo"))
				.collect(Collectors.toList());
		List<Object> requisitionStatuses = requisitionList.stream().map(req -> req.get("RequisitionStatus"))
				.collect(Collectors.toList());
		List<Object> requisitionIds = requisitionList.stream().map(req -> req.get("RequisitionId"))
				.collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, requisitionNos, requisitionStatuses, requisitionIds);
	}

	/**
	 * @Test4 This method retrieves and verifies the patient consumptions list.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and the patient consumptions list in the "Results"
	 *         field, containing details such as PatientId, HospitalNo, and
	 *         PatientVisitId.
	 */
	public CustomResponse getPatientConsumptions(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Directly extracting the fields PatientId, HospitalNo, and PatientVisitId
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> patientIds = results.stream().map(patient -> patient.get("PatientId"))
				.collect(Collectors.toList());
		List<Object> hospitalNos = results.stream().map(patient -> patient.get("HospitalNo"))
				.collect(Collectors.toList());
		List<Object> patientVisitIds = results.stream().map(patient -> patient.get("PatientVisitId"))
				.collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, patientIds, hospitalNos, patientVisitIds);
	}

	/**
	 * @Test5 This method retrieves and verifies the patient consumption
	 *        information.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - The API response includes the HTTP status code,
	 *         status message, and the patient consumption information in the
	 *         "Results" field, containing details such as PatientName, HospitalNo,
	 *         and StoreId.
	 */
	public CustomResponse getPatientConsumptionInfoByPatientIdAndVisitId(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extract PatientName, HospitalNo, and StoreId from the response
		Map<String, Object> results = response.jsonPath().getMap("Results");
		Map<String, Object> patientConsumption = (Map<String, Object>) results.get("PatientConsumption");

		Object patientName = patientConsumption.get("PatientName");
		Object hospitalNo = patientConsumption.get("HospitalNo");
		Object storeId = patientConsumption.get("StoreId");

		return new CustomResponse(response, statusCode, status, patientName, hospitalNo, storeId);
	}

	/**
	 * @Test6 This method retrieves and verifies the billing scheme by scheme ID.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         field, including SchemeCode, SchemeName, and SchemeId.
	 */
	public CustomResponse getBillingSchemeBySchemeId(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Directly extracting the fields SchemeCode, SchemeName, and SchemeId
		Map<String, Object> results = response.jsonPath().getMap("Results");

		Object schemeCode = results.get("SchemeCode");
		Object schemeName = results.get("SchemeName");
		Object schemeId = results.get("SchemeId");

		return new CustomResponse(response, statusCode, status, schemeCode, schemeName, schemeId);
	}

	/**
	 * @Test7 This method retrieves and verifies the billing summary by patient ID.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         field, including PatientId, TotalDue, and other billing details.
	 */
	public CustomResponse getBillingSummaryByPatientId(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields PatientId, TotalDue, and others from Results
		Map<String, Object> results = response.jsonPath().getMap("Results");
		Object patientId = results.get("PatientId");
		Object totalDue = results.get("TotalDue");

		return new CustomResponse(response, statusCode, status, patientId, totalDue);
	}

	/**
	 * @Test8 This method retrieves and verifies the consumptions list of a patient
	 *        by ID.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         field, including PatientConsumptionId, ConsumptionReceiptNo, and
	 *         TotalAmount.
	 */
	public CustomResponse getConsumptionsListOfAPatientById(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields PatientConsumptionId, ConsumptionReceiptNo, and
		// TotalAmount
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> patientConsumptionIds = results.stream().map(result -> result.get("PatientConsumptionId"))
				.collect(Collectors.toList());
		List<Object> consumptionReceiptNos = results.stream().map(result -> result.get("ConsumptionReceiptNo"))
				.collect(Collectors.toList());
		List<Object> totalAmounts = results.stream().map(result -> result.get("TotalAmount"))
				.collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, patientConsumptionIds, consumptionReceiptNos,
				totalAmounts);
	}

	/**
	 * @Test9 This method retrieves and verifies the return consumptions list.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         field, including ConsumptionReturnReceiptNo, HospitalNo, and
	 *         PatientId.
	 */
	public CustomResponse getReturnConsumptionsList(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields ConsumptionReturnReceiptNo, HospitalNo, and PatientId
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> consumptionReturnReceiptNos = results.stream()
				.map(result -> result.get("ConsumptionReturnReceiptNo")).collect(Collectors.toList());
		List<Object> hospitalNos = results.stream().map(result -> result.get("HospitalNo"))
				.collect(Collectors.toList());
		List<Object> patientIds = results.stream().map(result -> result.get("PatientId")).collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, consumptionReturnReceiptNos, hospitalNos, patientIds);
	}

	/**
	 * @Test10 This method retrieves and verifies the list of discharged patients.
	 * 
	 * @param endpoint - The API endpoint to which the GET request is sent.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         field, including VisitCode, PatientVisitId, and PatientId.
	 */
	public CustomResponse getDischargedPatients(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields VisitCode, PatientVisitId, and PatientId
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> visitCodes = results.stream().map(result -> result.get("VisitCode")).collect(Collectors.toList());
		List<Object> patientVisitIds = results.stream().map(result -> result.get("PatientVisitId"))
				.collect(Collectors.toList());
		List<Object> patientIds = results.stream().map(result -> result.get("PatientId")).collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, visitCodes, patientVisitIds, patientIds);
	}

	/**
	 * @Test11 This method retrieves the list of admitted patients.
	 * 
	 * @param endpoint - The API endpoint to retrieve the list of admitted patients.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getAdmittedPatients(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields PatientId, VisitId, and DischargeDate
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> patientIds = results.stream().map(result -> result.get("PatientId")).collect(Collectors.toList());
		List<Object> visitIds = results.stream().map(result -> result.get("VisitId")).collect(Collectors.toList());
		List<Object> dischargeDates = results.stream().map(result -> result.get("DischargeDate"))
				.collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, patientIds, visitIds, dischargeDates);
	}

	/**
	 * @Test12 This method retrieves IPD patients by a specific patient name.
	 * 
	 * @param endpoint - The API endpoint to retrieve IPD patients.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse searchIpdPatientByPatientId(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields PatientId, PatientCode from Results
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> patientIds = results.stream().map(result -> result.get("PatientId")).collect(Collectors.toList());
		List<Object> patientCodes = results.stream().map(result -> result.get("PatientCode"))
				.collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, patientIds, patientCodes);
	}

	/**
	 * @Test13 This method retrieves provisional patient information.
	 * 
	 * @param endpoint - The API endpoint to retrieve patients' provisional
	 *                 information.
	 * @param body     - Optional
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getPatientProvisionalInfo(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		if (body != null) {
			request.body(body);
		}

		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields PatientId and PatientCode
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> patientIds = results.stream().map(result -> result.get("PatientId")).collect(Collectors.toList());
		List<Object> patientCodes = results.stream().map(result -> result.get("PatientCode"))
				.collect(Collectors.toList());

		return new CustomResponse(response, statusCode, status, patientIds, patientCodes);
	}

	/**
	 * @Test14 This method retrieves the provisional items list for a specific
	 *         patient and scheme ID.
	 * 
	 * @param endpoint - The API endpoint to retrieve provisional items list.
	 * @param body     - Optional request body.
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getProvisionalItemsListByPatientIdAndSchemeId(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Include body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extract response details
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results.ProvisionalItems");

		// Return custom response
		return new CustomResponse(response, statusCode, status, results);
	}

	/**
	 * @Test15 This method retrieves the list of billing invoices within a specified
	 *         date range.
	 * 
	 * @param endpoint - The API endpoint to retrieve billing invoices.
	 * @param body     - Optional request body.
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getInvoicesByDateRange(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Include body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		System.out.println(response.prettyPrint());

		// Extract response details
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Extract specific fields
		List<Object> invoiceNumbers = results.stream().map(result -> result.get("InvoiceNumber"))
				.collect(Collectors.toList());
		List<Object> invoiceCodes = results.stream().map(result -> result.get("InvoiceCode"))
				.collect(Collectors.toList());

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, invoiceNumbers, invoiceCodes);
	}

	/**
	 * @Test16 This method retrieves the Providers list with authorization.
	 *
	 * @param endpoint - The API endpoint to retrieve the Providers list.
	 * @param body     - Optional request body.
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getProviderList(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Include body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extract response details
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extracting the fields EmployeeId and EmployeeName from Results
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		List<Object> employeeIds = results.stream().map(result -> result.get("EmployeeId"))
				.collect(Collectors.toList());
		List<Object> employeeNames = results.stream().map(result -> result.get("EmployeeName"))
				.collect(Collectors.toList());

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, employeeIds, employeeNames);
	}

	/**
	 * @Test17 This method retrieves the Users list with authorization.
	 *
	 * @param endpoint - The API endpoint to retrieve the Users list.
	 * @param body     - Optional request body.
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getUsersList(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Include body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extract response details
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");
		List<Map<String, Object>> results = response.jsonPath().getList("Results");

		// Extract specific fields
		List<Object> userIds = results.stream().map(result -> result.get("UserId")).collect(Collectors.toList());
		List<Object> shortNames = results.stream().map(result -> result.get("ShortName")).collect(Collectors.toList());
		List<Object> departmentNames = results.stream().map(result -> result.get("DepartmentName"))
				.collect(Collectors.toList());

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, userIds, shortNames, departmentNames);
	}

	/**
	 * @Test18 This method retrieves the current fiscal year details with
	 *         authorization.
	 *
	 * @param endpoint - The API endpoint to retrieve the current fiscal year
	 *                 details.
	 * @param body     - Optional request body.
	 * @return CustomResponse - Contains HTTP status, status message, and Results
	 *         list.
	 */
	public CustomResponse getCurrentFiscalYearDetails(String endpoint, Object body) {
		RequestSpecification request = RestAssured.given().header("Authorization", AuthUtil.getAuthHeader())
				.header("Content-Type", "application/json");

		// Include body if it's not null
		if (body != null) {
			request.body(body);
		}

		// Send GET request and extract response
		Response response = request.get(BASE_URL + endpoint).then().extract().response();

		// Extract response details
		int statusCode = response.statusCode();
		String status = response.jsonPath().getString("Status");

		// Extract the specific fields FiscalYearId and FiscalYearName
		Map<String, Object> mapResults = response.jsonPath().getMap("Results");
		Object fiscalYearId = mapResults.get("FiscalYearId");
		Object fiscalYearName = mapResults.get("FiscalYearName");

		// Return custom response with extracted fields
		return new CustomResponse(response, statusCode, status, fiscalYearId, fiscalYearName);
	}

}