/*
 * Bluegiga’s Bluetooth Smart Android SW for Bluegiga BLE modules
 * Contact: support@bluegiga.com.
 *
 * This is free software distributed under the terms of the MIT license reproduced below.
 *
 * Copyright (c) 2013, Bluegiga Technologies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files ("Software")
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF
 * ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A  PARTICULAR PURPOSE.
 */
package com.flowehealth.efr_version.features.iop_test.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.flowehealth.efr_version.R
import com.flowehealth.efr_version.features.iop_test.activities.IOPTestActivity
import com.flowehealth.efr_version.features.iop_test.adapters.IOPTestAdapter
import com.flowehealth.efr_version.features.iop_test.models.IOPTest
import com.flowehealth.efr_version.features.iop_test.models.ItemTestCaseInfo
import com.flowehealth.efr_version.common.other.CardViewListDecoration
import kotlinx.android.synthetic.main.fragment_iop_test.*
import java.util.*

class IOPTestFragment : Fragment(R.layout.fragment_iop_test), IOPTestActivity.Listener {
    private lateinit var testAdapter: IOPTestAdapter
    private var listItemTest: List<ItemTestCaseInfo> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listItemTest = IOPTest.getSiliconLabsTestInfo().listItemTest
    }

    private fun initAdapter() {
        testAdapter = IOPTestAdapter(listItemTest)
        rv_iop_tests.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(CardViewListDecoration())
            this.adapter = testAdapter
        }
    }

    override fun scrollViewToPosition(position: Int) {
        rv_iop_tests.post {
            rv_iop_tests.smoothScrollToPosition(position)
        }
    }

    private fun setListener() {
        (activity as IOPTestActivity?)?.setListener(this)
    }

    override fun updateUi() {
        activity?.runOnUiThread {
            testAdapter.refreshDataItem(IOPTest.getSiliconLabsTestInfo().listItemTest)
        }
    }

    companion object {
        fun newInstance(): IOPTestFragment {
            return IOPTestFragment()
        }
    }
}