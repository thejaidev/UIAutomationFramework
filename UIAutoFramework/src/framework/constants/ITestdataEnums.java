package framework.constants;

/**
 * IFrameworkConstants interface contains base suite reference, browser
 * references, browser, test data, and generic enums
 */
public interface ITestdataEnums extends IFrameworkConstants {

	/**
	 * Sheet names of TestData.xls
	 *
	 * @note String provided in the double quotes should be the ("<Sheet name in
	 *       TestData.xls>")
	 */
	public enum Sheetname {

		TEST_MAP("TestMap"),
		TEST_DATA("Testdata");

		private String name;

		Sheetname(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Column names of the TestMap sheet in TestData.xlsx
	 *
	 * @note String provided in the double quotes should be the ("<Column name in
	 *       TestData.xls>")
	 */
	public enum Testmap {
		SCRIPTNAME("ScriptName"),
		ITERATION("Iteration"),
		TEST_SCENARIO("TestScenario"),
		TEST_SCENARIO_DESC("TestScenarioDescription"),
		EXECUTE("Execute"),
		TYPE("Type");

		private String name;

		Testmap(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Column names of the Testdata sheet in TestData.xlsx
	 *
	 * @note String provided in the double quotes should be the ("<Column name in
	 *       TestData.xls>")
	 */
	public enum Testdata {
		SCRIPTNAME("ScriptName"),
		ITERATION("Iteration"),
		BROWSER("Browser"),
		USERNAME("Username"),
		PASSWORD("Password");

		private String name;

		Testdata(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Keywords used in test data excel
	 */
	public enum TestdataKeyword {

		EMPTY("<EMPTY>"),
		GENERATE("<GENERATE>");

		private String name;

		TestdataKeyword(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 *         Enums for Parameter Sheet Keys and Definitions
	 */
	enum ParameterSheet {
		VPDX("VPDX"),
		VPFX("VPFX"),
		VPDX_REPLACE("VPDXReplace"),
		VPFX_REPLACE("VPFXReplace"),
		VPFX_DESC("VPFXDescription"),
		VPFX_DESC_REPLACE("VPFXDescriptionReplace"),
		CONTAINER_NODE("ContainerNode"),
		CONTAINER_NAME("ContainerName"),
		PARAM_TEMPLATE_NAME("ParameterTemplateName"),
		PARAM_TEMPLATE_DESC("ParameterTemplateDescription"),
		DEFAULT_PARAM_NAME("Default-ParameterName"),
		ADD_PARAM_NAME("Add-ParameterName"),
		EDIT_PARAM_NAME("Edit-ParameterName"),
		DELETE_PARAM_NAME("DeleteParameter"),
		REPLACE_PARM_NAME("Replace-ParameterName"),
		PARAM_XPATH("ParameterXPATH"),
		PARAM_TEMPLATE_OVERRIDE("ParameterTemplateOverride"),
		APP_NAME("ApplicationName");

		private String name;

		ParameterSheet(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 *         Enums for Parameter Sheet Values
	 */
	public enum ParameterSheetValue {
		ADD_PARAM_VALUE_UI("Add-ParameterValueUI"),
		EDIT_PARAM_VALUE_UI("Edit-ParameterValueUI"),
		PARAM_VALUE_MSG_SRV_DEFAULT("ParameterValueMsgSrvDefault"),
		PARAM_VALUE_MSG_SRV_ADD("ParameterValueMsgSrvAdd"),
		PARAM_VALUE_MSG_SRV_EDIT("ParameterValueMsgSrvEdit"),
		PARAM_VALUE_MSG_SRV_DELETE("ParameterValueMsgSrvDelete"),
		PARAM_CARRY_FWD_VALUE("ParameterCarryForwardValueMsgSrv"),
		PARAM_REPLACE_VALUE("ParameterReplaceValueMsgSrv");

		private String name;

		ParameterSheetValue(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 *         Enums for Parameter Sheet releated Operation Values
	 */
	enum ParameterTestDataOptions {
		NO_VALUE("NoValue"),
		SKIP_VALUE("SkipValue"),
		NEW_UPLOAD("NewUpload");

		private String name;

		ParameterTestDataOptions(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Column names of the EventManagement sheet in TestData.xlsx
	 *
	 * @note String provided in the double quotes should be the ("<Column name in
	 *       TestData.xls>")
	 */
	public enum EventManagement {
		ALERT_NAME("AlertName"),
		ALERT_SERVERITY("AlertSeverity"),
		ALERT_STATUS("AlertStatus"),
		ALERT_THRESHOLD("AlertThreshold"),
		ALERT_AUTOCLOSE("AlertAutoClose"),
		ALERT_ALWAYS_GENERATE_NEWALERT("AlertAlwaysGenerateNewAlert"),
		DEVICE_EVENTS("DeviceEvents"),
		DOCK_TO_ID("DockedToId"),
		DOCK_TO_MODEL("DockedToModel"),
		APP_NAME("AppName"),
		SEVERITY("Severity");

		private String name;

		EventManagement(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Column names of the Import sheet in TestData.xlsx
	 *
	 * @note String provided in the double quotes should be the ("<Column name in
	 * TestData.xls>")
	 *
	 */
	public enum Import {

		FILE_NM("FileName"),
		VHQ_DATA_VERSION("VHQDataVersion"),
		HIERARCHY_OPERATION("HierarchyOperation"),
		HIERARCHY_NAME("HierarchyName"),
		HIERARCHY_DESCRIPTION("HierarchyDescription"),
		PARENT_HIERARCHY_PATH("ParentHierarchyPath"),
		TIMEZONE("TimeZone"),
		IP_ADDRESS_START("IPAddressStart"),
		IP_ADDRESS_END("IPAddressEnd"),
		HIERARCHY_AUTOMATION_ENABLED("HierarchyAutomationEnabled"),
		DOWNLOAD_ON("DownloadOn"),
		INHERIT_FROM_PARENT_HIERARCHY("InheritFromParentHierarchy"),
		HIERARCHY_SOFTWARE_OPERATION("HierarchySoftwareOperation"),
		HIERARCHY_SOFTWARE_ASSOCIATION_TYPE("HierarchySoftwareAssociationType"),
		HIERARCHY_REFERENCE_SET("HierarchyReferenceSetName"),
		GROUP_OPERATION("GroupOperation"),
		GROUP_NAME("GroupName"),
		DESCRIPTION("Description"),
		DEVICE_OPERATION("DeviceOperation"),
		SERIAL("SN"),
		DEVICE_ID("DID"),
		MODEL("Model"),
		IDENTIFIER_TYPE("IdentifierType"),
		DEVICE_HIERARCHY_PATH("DeviceHierarchyPath"),
		DEVICE_HIERARCHY_NAME("DeviceHierarchyName"),
		DEVICE_IP_ADDRESS("DeviceIPAddress"),
		STATUS("Status"),
		DEVICE_GROUPS("DeviceGroups"),
		DEVICE_AUTOMATION_ENABLED("DeviceAutomationEnabled"),
		ASSOCIATION_TYPE("AssociationType"),
		REFERENCE_SET_NM("ReferenceSetName"),
		PACKAGE_OPERATION("PackageOperation"),
		PACKAGE_NAME("PackageName"),
		APPLICATION_OPERATION("ApplicationOperation"),
		APPLICATION_NAME("ApplicationName"),
		APPLICATION_VERSION("ApplicationVersion"),
		APPLICATION_ID("ApplicationID"),
		PARAMETER_TEMPLATE_NAME("ParameterTemplateName"),
		PARAMETER_OPERATIONS("ParametersOperation"),
		PARAMETER_NAME("ParameterName"),
		PARAMETER_VALUE("ParameterValue"),
		PARAMETER_TEMPLATE_IMPORT_NAME("ParameterTemplateImportName"),
		PARAMETER_TEMPLATE_IMPORT_OPERATION("ParameterTemplateImportOperation"),
		PARAMETER_TEMPLATE_IMPORT_DESC("ParameterTemplateImportDescription"),
		PARAMETER_TEMPLATE_OVERRIDE("ParameterTemplateOverride");

		private String name;

		Import(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Enum for all the Default Values present in Test Data
	 *
	 */
	enum TestDataDefaultValues {
		COMPANY_NAME_CONFIG("CompanyNameFromConfig"),
		PARENT_HIERARCHY_NAME_CONFIG("ParentHierarchyNameFromConfig"),
		LOGIN_USERNAME("LoginUsernameConfig"),
		LOGIN_PASSWORD("LoginPasswordConfig"),
		BROWSER("ConfigBrowser");

		private String name;

		TestDataDefaultValues(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	/**
	 * Enum for System Configuration Name in Database
	 */
	public enum SystemConfigurationDatabaseNames {

		DEVICE_READY_TO_WAIT("Device Ready To Wait For MP");

		private String name;

		SystemConfigurationDatabaseNames(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}
}
