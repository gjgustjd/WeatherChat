package com.miso.misoweather.common

import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

import org.junit.Assert.*

class CommonUtilTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun toIntString() {
        assertThat(CommonUtil.toIntString("1.00002202032")).isEqualTo("1")
        assertThat(CommonUtil.toIntString("2.00002202032")).isEqualTo("2")
        assertThat(CommonUtil.toIntString("")).isEqualTo("0")
        assertThat(CommonUtil.toIntString("dddd")).isEqualTo("0")
    }
}