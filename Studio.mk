LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := SetupWizardStudio
LOCAL_MODULE_CLASS := FAKE
LOCAL_MODULE_SUFFIX := -timestamp

gen_studio_tool_path := $(abspath $(LOCAL_PATH))/gen-studio.sh

setupwizard_system_libs_path := $(abspath $(LOCAL_PATH))/system_libs
setupwizard_system_libs_deps := $(call java-lib-deps,core-oj) \
                           $(call java-lib-deps,core-libart) \
                           $(call java-lib-deps,telephony-common) \
                           $(call java-lib-deps,org.lineageos.platform.internal) \
                           $(call java-lib-deps,setupcompat) \
                           $(call java-lib-deps,setupdesign)

setupwizard_system_res_path :=
setupwizard_system_res_deps :=

setupwizard_library_replaces :=

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE): $(setupwizard_system_libs_deps)
	$(hide) $(gen_studio_tool_path) "$(setupwizard_system_libs_path)" "$(setupwizard_system_libs_deps)" "$(setupwizard_system_res_path)" "$(setupwizard_system_res_deps)" "$(setupwizard_library_replaces)"
	$(hide) echo "Fake: $@"
	$(hide) mkdir -p $(dir $@)
	$(hide) touch $@
