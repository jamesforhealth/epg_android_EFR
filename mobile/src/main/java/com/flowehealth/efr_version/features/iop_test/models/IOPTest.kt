package com.flowehealth.efr_version.features.iop_test.models

class IOPTest {
    companion object {
        private lateinit var mSiliconLabsTestInfo: SiliconLabsTestInfo

        fun getSiliconLabsTestInfo(): SiliconLabsTestInfo {
            return mSiliconLabsTestInfo
        }

        fun getItemTestCaseInfo(position: Int): ItemTestCaseInfo {
            return mSiliconLabsTestInfo.listItemTest[position]
        }

        fun getListItemChildrenTest(position: Int): List<ChildrenItemTestInfo>? {
            return mSiliconLabsTestInfo.listItemTest[position].listChildrenItem
        }

        fun createDataTest(fwName: String) {
            mSiliconLabsTestInfo = DemoItemProvider.createDataSiliconLabsTest(fwName)
        }
    }
}