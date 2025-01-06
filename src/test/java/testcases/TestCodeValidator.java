package testcases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rest.CustomResponse;

public class TestCodeValidator {

	// Method to validate if specific keywords are used in the method's source code
	public static boolean validateTestMethodFromFile(String filePath, String methodName, List<String> keywords)
			throws IOException {
		// Read the content of the test class file
		String fileContent = new String(Files.readAllBytes(Paths.get(filePath)));

		// Extract the method body for the specified method using regex
		String methodRegex = "(public\\s+CustomResponse\\s+" + methodName + "\\s*\\(.*?\\)\\s*\\{)([\\s\\S]*?)}";
		Pattern methodPattern = Pattern.compile(methodRegex);
		Matcher methodMatcher = methodPattern.matcher(fileContent);

		if (methodMatcher.find()) {

			String methodBody = fetchBody(filePath, methodName);

			// Now we validate the method body for the required keywords
			boolean allKeywordsPresent = true;

			// Loop over the provided keywords and check if each one is present in the
			// method body
			for (String keyword : keywords) {
				Pattern keywordPattern = Pattern.compile("\\b" + keyword + "\\s*\\(");
				if (!keywordPattern.matcher(methodBody).find()) {
					System.out.println("'" + keyword + "()' is missing in the method.");
					allKeywordsPresent = false;
				}
			}

			return allKeywordsPresent;

		} else {
			System.out.println("Method " + methodName + " not found in the file.");
			return false;
		}
	}

	// This method takes the method name as an argument and returns its body as a
	// String.
	public static String fetchBody(String filePath, String methodName) {
		StringBuilder methodBody = new StringBuilder();
		boolean methodFound = false;
		boolean inMethodBody = false;
		int openBracesCount = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Check if the method is found by matching method signature
				if (line.contains("public CustomResponse " + methodName + "(")
						|| line.contains("public String " + methodName + "(")
						|| line.contains("public Response " + methodName + "(")) {
					methodFound = true;
				}

				// Once the method is found, start capturing lines
				if (methodFound) {
					if (line.contains("{")) {
						inMethodBody = true;
						openBracesCount++;
					}

					// Capture the method body
					if (inMethodBody) {
						methodBody.append(line).append("\n");
					}

					// Check for closing braces to identify the end of the method
					if (line.contains("}")) {
						openBracesCount--;
						if (openBracesCount == 0) {
							break; // End of method body
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return methodBody.toString();
	}

	public static boolean validateResponseFields(String methodName, CustomResponse customResponse) {
		boolean isValid = true;

		switch (methodName) {
		case "getAllStocks":
			List<String> expectedStockFields = List.of("ItemId", "ItemName", "GenericName", "SalePrice", "CostPrice");

			List<Map<String, Object>> stockResults = customResponse.getResponse().jsonPath().getList("Results");
			if (stockResults == null || stockResults.isEmpty()) {
				isValid = false;
				System.out.println("Results section is missing or empty in the response.");
				break;
			}

			for (int i = 0; i < stockResults.size(); i++) {
				Map<String, Object> stock = stockResults.get(i);
				for (String field : expectedStockFields) {
					if (!stock.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in Results[" + i + "]: " + field);
					}
				}
			}

			// Validate top-level fields
			String stockStatusField = customResponse.getResponse().jsonPath().getString("Status");
			if (stockStatusField == null || !stockStatusField.equals("OK")) {
				isValid = false;
				System.out.println("Status field is missing or invalid in the response.");
			}
			break;

		case "getMainStore":
			List<String> mainStoreFields = List.of("StoreId", "Category", "IsActive");

			// Validate that each field is not null in the CustomResponse
			for (String field : mainStoreFields) {
				if (field.equals("StoreId") && customResponse.getStoreId() == null) {
					isValid = false;
					System.out.println("Missing or null field: StoreId");
				}
				if (field.equals("Category") && customResponse.getCategory() == null) {
					isValid = false;
					System.out.println("Missing or null field: Category");
				}
				if (field.equals("IsActive") && customResponse.getIsActive() == null) {
					isValid = false;
					System.out.println("Missing or null field: IsActive");
				}
			}
			break;

		case "getRequisitionByDateRange":
			List<String> requisitionFields = List.of("RequisitionNo", "RequisitionStatus", "RequisitionId");

			// Get requisition field lists directly from the CustomResponse object
			List<Object> requisitionNos = customResponse.getItemIds(); // Ensure this method exists
			List<Object> requisitionStatuses = customResponse.getItemNames(); // Ensure this method exists
			List<Object> requisitionIds = customResponse.getGenericNames(); // Ensure this method exists

			// Validate that requisition fields are not null
			for (int i = 0; i < requisitionNos.size(); i++) {
				if (requisitionNos.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: RequisitionNo at index " + i);
				} else {
					System.out.println("RequisitionNo at index " + i + ": " + requisitionNos.get(i));
				}

				if (requisitionStatuses.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: RequisitionStatus at index " + i);
				} else {
					System.out.println("RequisitionStatus at index " + i + ": " + requisitionStatuses.get(i));
				}

				if (requisitionIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: RequisitionId at index " + i);
				} else {
					System.out.println("RequisitionId at index " + i + ": " + requisitionIds.get(i));
				}
			}

			// Validate that requisition IDs are unique
			Set<Object> uniqueRequisitionIds = new HashSet<>(requisitionIds);
			if (uniqueRequisitionIds.size() != requisitionIds.size()) {
				isValid = false;
				System.out.println("Requisition IDs are not unique.");
			} else {
				System.out.println("Requisition IDs are unique.");
			}

			break;

		case "getPatientConsumptions":
			List<String> patientConsumptionFields = List.of("PatientId", "HospitalNo", "PatientVisitId");

			// Get the lists directly from the CustomResponse object
			List<Object> patientIds = customResponse.getItemIds();
			List<Object> hospitalNos = customResponse.getItemNames();
			List<Object> patientVisitIds = customResponse.getGenericNames();

			// Validate that PatientId, HospitalNo, and PatientVisitId are not null
			for (int i = 0; i < patientIds.size(); i++) {
				if (patientIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId at index " + i);
				}
				if (hospitalNos.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: HospitalNo at index " + i);
				}
				if (patientVisitIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientVisitId at index " + i);
				}
			}

			// Validate that none of the lists are empty
			if (patientIds.isEmpty()) {
				isValid = false;
				System.out.println("PatientIds list should not be empty.");
			}
			if (hospitalNos.isEmpty()) {
				isValid = false;
				System.out.println("HospitalNos list should not be empty.");
			}
			if (patientVisitIds.isEmpty()) {
				isValid = false;
				System.out.println("PatientVisitIds list should not be empty.");
			}

			break;

		case "getPatientConsumptionInfoByPatientIdAndVisitId":
			// Expected fields for "PatientConsumption" and "PatientConsumptionItems"
			List<String> patientConsumptionField = List.of("PatientId", "PatientName", "HospitalNo", "StoreId");
			List<String> patientConsumptionItemFields = List.of("PatientConsumptionItemId", "ItemId", "ItemName",
					"Quantity", "SalePrice", "TotalAmount", "BatchNo", "ExpiryDate", "StoreId");

			// Retrieve the "Results" section
			Map<String, Object> results = customResponse.getResponse().jsonPath().getMap("Results");

			// Debugging: Print the actual structure
			System.out.println("Actual Results Keys: " + results.keySet());

			// Validate "PatientConsumption" fields
			Map<String, Object> patientConsumption = (Map<String, Object>) results.get("PatientConsumption");
			if (patientConsumption == null) {
				isValid = false;
				System.out.println("Missing 'PatientConsumption' section in Results.");
			} else {
				System.out.println("PatientConsumption Fields: " + patientConsumption.keySet());
				for (String field : patientConsumptionField) {
					if (!patientConsumption.containsKey(field)) {
						isValid = false;
						System.out.println("Missing field in PatientConsumption: " + field);
					}
				}
			}

			// Validate "PatientConsumptionItems" array
			List<Map<String, Object>> patientConsumptionItems = (List<Map<String, Object>>) results
					.get("PatientConsumptionItems");
			if (patientConsumptionItems == null || patientConsumptionItems.isEmpty()) {
				isValid = false;
				System.out.println("Missing or empty 'PatientConsumptionItems' in Results.");
			} else {
				for (int i = 0; i < patientConsumptionItems.size(); i++) {
					Map<String, Object> item = patientConsumptionItems.get(i);
					System.out.println("PatientConsumptionItem[" + i + "] Fields: " + item.keySet());
					for (String field : patientConsumptionItemFields) {
						if (!item.containsKey(field)) {
							isValid = false;
							System.out.println("Missing field in PatientConsumptionItems[" + i + "]: " + field);
						}
					}
				}
			}
			break;

		case "getBillingSchemeBySchemeId":
			List<String> billingSchemeFields = List.of("SchemeCode", "SchemeName", "SchemeId");

			// Get the fields directly from the CustomResponse object
			Object schemeCode = customResponse.getStoreId();
			Object schemeName = customResponse.getCategory();
			Object schemeId = customResponse.getIsActive();

			// Validate that SchemeCode, SchemeName, and SchemeId are not null
			for (String field : billingSchemeFields) {
				if (field.equals("SchemeCode") && schemeCode == null) {
					isValid = false;
					System.out.println("Missing or null field: SchemeCode");
				}
				if (field.equals("SchemeName") && schemeName == null) {
					isValid = false;
					System.out.println("Missing or null field: SchemeName");
				}
				if (field.equals("SchemeId") && schemeId == null) {
					isValid = false;
					System.out.println("Missing or null field: SchemeId");
				}
			}

			// Optionally, you can check if the SchemeId matches the requested value.
			if (schemeId != null && !schemeId.toString().equals("4")) { // Example validation for schemeId
				isValid = false;
				System.out.println("SchemeId does not match the requested value.");
			}
			break;

		case "getBillingSummaryByPatientId":
			List<String> billingSummaryFields = List.of("PatientId", "TotalDue");

			// Get the fields directly from the CustomResponse object
			Object patientId = customResponse.getPatientId();
			Object totalDue = customResponse.getTotalDue();

			// Validate that PatientId and TotalDue are not null
			for (String field : billingSummaryFields) {
				if (field.equals("PatientId") && patientId == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId");
				}
				if (field.equals("TotalDue") && totalDue == null) {
					isValid = false;
					System.out.println("Missing or null field: TotalDue");
				}
			}

			// Optionally, you can check if the PatientId matches the requested value.
			if (patientId != null && !patientId.toString().equals("114")) { // Example validation for patientId
				isValid = false;
				System.out.println("PatientId does not match the requested value.");
			}
			break;

		case "getConsumptionsListOfAPatientById":
			List<String> consumptionsFields = List.of("PatientConsumptionId", "ConsumptionReceiptNo", "TotalAmount");

			// Get the fields directly from the CustomResponse object
			List<Object> patientConsumptionIds = customResponse.getItemIds();
			List<Object> consumptionReceiptNos = customResponse.getItemNames();
			List<Object> totalAmounts = customResponse.getGenericNames();

			// Validate that PatientConsumptionId, ConsumptionReceiptNo, and TotalAmount are
			// not null
			for (int i = 0; i < patientConsumptionIds.size(); i++) {
				if (patientConsumptionIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientConsumptionId at index " + i);
				}
				if (consumptionReceiptNos.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: ConsumptionReceiptNo at index " + i);
				}
				if (totalAmounts.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: TotalAmount at index " + i);
				}
			}

			// Check for uniqueness of PatientConsumptionId
			Set<Object> uniquePatientConsumptionIds = new HashSet<>(patientConsumptionIds);
			if (uniquePatientConsumptionIds.size() != patientConsumptionIds.size()) {
				isValid = false;
				System.out.println("PatientConsumptionId values are not unique.");
			}
			break;

		case "getReturnConsumptionsList":
			List<String> returnConsumptionsFields = List.of("ConsumptionReturnReceiptNo", "HospitalNo", "PatientId");

			// Get the fields directly from the CustomResponse object
			List<Object> consumptionReturnReceiptNos = customResponse.getItemIds();
			List<Object> hospitalNoss = customResponse.getItemNames();
			List<Object> patientIdss = customResponse.getGenericNames();

			// Validate that ConsumptionReturnReceiptNo, HospitalNo, and PatientId are not
			// null
			for (int i = 0; i < consumptionReturnReceiptNos.size(); i++) {
				if (consumptionReturnReceiptNos.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: ConsumptionReturnReceiptNo at index " + i);
				}
				if (hospitalNoss.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: HospitalNo at index " + i);
				}
				if (patientIdss.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId at index " + i);
				}
			}

			// Check for uniqueness of ConsumptionReturnReceiptNo
			Set<Object> uniqueConsumptionReturnReceiptNos = new HashSet<>(consumptionReturnReceiptNos);
			if (uniqueConsumptionReturnReceiptNos.size() != consumptionReturnReceiptNos.size()) {
				isValid = false;
				System.out.println("ConsumptionReturnReceiptNo values are not unique.");
			}
			break;

		case "getDischargedPatients":
			List<String> dischargedPatientsFields = List.of("VisitCode", "PatientVisitId", "PatientId");

			// Get the fields directly from the CustomResponse object
			List<Object> visitCodes = customResponse.getItemIds();
			List<Object> patientVisitIdss = customResponse.getItemNames();
			List<Object> patientIdsss = customResponse.getGenericNames();

			// Validate that VisitCode, PatientVisitId, and PatientId are not null
			for (int i = 0; i < visitCodes.size(); i++) {
				if (visitCodes.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: VisitCode at index " + i);
				}
				if (patientVisitIdss.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientVisitId at index " + i);
				}
				if (patientIdsss.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId at index " + i);
				}
			}

			// Check for uniqueness of PatientVisitId
			Set<Object> uniquePatientVisitIds = new HashSet<>(patientVisitIdss);
			if (uniquePatientVisitIds.size() != patientVisitIdss.size()) {
				isValid = false;
				System.out.println("PatientVisitId values are not unique.");
			}
			break;

		case "getAdmittedPatients":
			List<String> admittedPatientsFields = List.of("PatientId", "VisitId", "DischargeDate");

			// Get the fields directly from the CustomResponse object
			List<Object> patIds = customResponse.getItemIds();
			List<Object> visitIds = customResponse.getItemNames();
			List<Object> dischargeDates = customResponse.getGenericNames();

			// Validate that PatientId, VisitId, and DischargeDate are not null
			for (int i = 0; i < patIds.size(); i++) {
				if (patIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId at index " + i);
				}
				if (visitIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: VisitId at index " + i);
				}
				if (dischargeDates.get(i) != null) {
					isValid = false;
					System.out.println("DischargeDate should be null for admitted patients at index " + i);
				}
			}

			// Check for uniqueness of PatientId and VisitId
			Set<Object> uniquePatientIds = new HashSet<>(patIds);
			Set<Object> uniqueVisitIds = new HashSet<>(visitIds);
			if (uniquePatientIds.size() != patIds.size()) {
				isValid = false;
				System.out.println("PatientId values are not unique.");
			}
			if (uniqueVisitIds.size() != visitIds.size()) {
				isValid = false;
				System.out.println("VisitId values are not unique.");
			}
			break;

		case "searchIpdPatientByPatientId":
			List<String> ipdPatientFields = List.of("PatientId", "PatientCode");

			// Get the fields directly from the CustomResponse object
			List<Object> patientIds1 = customResponse.getPatientIds();
			List<Object> patientCodes = customResponse.getPatientCodes();

			// Validate that PatientId and PatientCode are not null
			for (int i = 0; i < patientIds1.size(); i++) {
				if (patientIds1.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId at index " + i);
				}
				if (patientCodes.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientCode at index " + i);
				}
			}

			// Check for uniqueness of PatientId and PatientCode
			Set<Object> uniquePatientIds1 = new HashSet<>(patientIds1);
			Set<Object> uniquePatientCodes = new HashSet<>(patientCodes);
			if (uniquePatientIds1.size() != patientIds1.size()) {
				isValid = false;
				System.out.println("PatientId values are not unique.");
			}
			if (uniquePatientCodes.size() != patientCodes.size()) {
				isValid = false;
				System.out.println("PatientCode values are not unique.");
			}
			break;

		case "getPatientProvisionalInfo":
			List<String> provisionalInfoFields = List.of("PatientId", "PatientCode");

			// Get the fields directly from the CustomResponse object
			List<Object> patientIds11 = customResponse.getPatientIds();
			List<Object> patientCodes1 = customResponse.getPatientCodes();

			// Validate that PatientId and PatientCode are not null
			for (int i = 0; i < patientIds11.size(); i++) {
				if (patientIds11.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId at index " + i);
				}
				if (patientCodes1.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientCode at index " + i);
				}
			}

			// Check for uniqueness of PatientId and PatientCode
			Set<Object> uniquePatientIds11 = new HashSet<>(patientIds11);
			Set<Object> uniquePatientCodes1 = new HashSet<>(patientCodes1);
			if (uniquePatientIds11.size() != patientIds11.size()) {
				isValid = false;
				System.out.println("PatientId values are not unique.");
			}
			if (uniquePatientCodes1.size() != patientCodes1.size()) {
				isValid = false;
				System.out.println("PatientCode values are not unique.");
			}
			break;

		case "getProvisionalItemsListByPatientIdAndSchemeId":
			List<String> provisionalItemsFields = List.of("PatientId");

			// Get the provisional items directly from the CustomResponse object
			List<Map<String, Object>> provisionalItems = customResponse.getListResults();

			// Validate that PatientId is not null and matches the expected value
			for (Map<String, Object> item : provisionalItems) {
				Object patientIdObj = item.get("PatientId");

				if (patientIdObj == null) {
					isValid = false;
					System.out.println("Missing or null field: PatientId");
				} else {
					// Ensure PatientId is an Integer and compare it with the requested patientId
					Integer patientId1 = (patientIdObj instanceof Integer) ? (Integer) patientIdObj : null;

					if (patientId1 == null) {
						// If the PatientId is not an Integer, try to parse it from a String
						try {
							patientId1 = Integer.valueOf(patientIdObj.toString());
						} catch (NumberFormatException e) {
							isValid = false;
							System.out.println("PatientId could not be parsed to an Integer.");
						}
					}

					// Direct comparison of PatientId without using parseInt
					if (patientId1 != null && !patientId1.equals(Integer.valueOf(patientId1.toString()))) {
						isValid = false;
						System.out.println("PatientId in ProvisionalItems does not match the requested PatientId.");
					}
				}
			}

			// Validate that ProvisionalItems list is not empty
			if (provisionalItems.isEmpty()) {
				isValid = false;
				System.out.println("ProvisionalItems list should not be empty.");
			}

			break;

		case "getInvoicesByDateRange":
			List<String> invoiceFields = List.of("InvoiceNumber", "InvoiceCode");

			// Get the fields directly from the CustomResponse object
			List<Object> invoiceNumbers = customResponse.getPatientIds();
			List<Object> invoiceCodes = customResponse.getPatientCodes();

			// Validate that InvoiceNumber and InvoiceCode are not null
			for (int i = 0; i < invoiceNumbers.size(); i++) {
				if (invoiceNumbers.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: InvoiceNumber at index " + i);
				}
				if (invoiceCodes.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: InvoiceCode at index " + i);
				}
			}
			break;

		case "getProviderList":
			List<String> providerFields = List.of("EmployeeId", "EmployeeName");

			// Get the fields directly from the CustomResponse object
			List<Object> employeeIds = customResponse.getPatientIds();
			List<Object> employeeNames = customResponse.getPatientCodes();

			// Validate that EmployeeId and EmployeeName are not null
			for (int i = 0; i < employeeIds.size(); i++) {
				if (employeeIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: EmployeeId at index " + i);
				}
				if (employeeNames.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: EmployeeName at index " + i);
				}
			}

			// Check for uniqueness of EmployeeId and EmployeeName
			Set<Object> uniqueEmployeeIds = new HashSet<>(employeeIds);
			Set<Object> uniqueEmployeeNames = new HashSet<>(employeeNames);
			if (uniqueEmployeeIds.size() != employeeIds.size()) {
				isValid = false;
				System.out.println("EmployeeId values are not unique.");
			}
			if (uniqueEmployeeNames.size() != employeeNames.size()) {
				isValid = false;
				System.out.println("EmployeeName values are not unique.");
			}
			break;

		case "getUsersList":
			List<String> userFields = List.of("UserId", "ShortName", "DepartmentName");

			// Get the fields directly from the CustomResponse object
			List<Object> userIds = customResponse.getItemIds();
			List<Object> shortNames = customResponse.getItemNames();
			List<Object> departmentNames = customResponse.getGenericNames();

			// Validate that UserId, ShortName, and DepartmentName are not null
			for (int i = 0; i < userIds.size(); i++) {
				if (userIds.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: UserId at index " + i);
				}
				if (shortNames.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: ShortName at index " + i);
				}
				if (departmentNames.get(i) == null) {
					isValid = false;
					System.out.println("Missing or null field: DepartmentName at index " + i);
				}
			}

			// Check for uniqueness of UserId, ShortName, and DepartmentName
			Set<Object> uniqueUserIds = new HashSet<>(userIds);
			Set<Object> uniqueShortNames = new HashSet<>(shortNames);
			Set<Object> uniqueDepartmentNames = new HashSet<>(departmentNames);

			if (uniqueUserIds.size() != userIds.size()) {
				isValid = false;
				System.out.println("UserId values are not unique.");
			}
			if (uniqueShortNames.size() != shortNames.size()) {
				isValid = false;
				System.out.println("ShortName values are not unique.");
			}
			break;

		case "getCurrentFiscalYearDetails":
			// Get the fields directly from the CustomResponse object
			Object fiscalYearId = customResponse.getPatientId();
			Object fiscalYearName = customResponse.getTotalDue();

			// Validate that FiscalYearId and FiscalYearName are not null
			if (fiscalYearId == null) {
				isValid = false;
				System.out.println("Missing or null field: FiscalYearId");
			}
			if (fiscalYearName == null) {
				isValid = false;
				System.out.println("Missing or null field: FiscalYearName");
			}
			break;

		default:
			System.out.println("Method " + methodName + " is not recognized for validation.");
			isValid = false;
		}
		return isValid;
	}

}