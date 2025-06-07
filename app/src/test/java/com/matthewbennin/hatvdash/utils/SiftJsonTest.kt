package com.matthewbennin.hatvdash.utils

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SiftJsonTest {
    @Test
    fun extractViewJson_returnsExpectedView() {
        val lovelaceJson = JSONObject(
            """{
                "views": [
                    {"path": "home", "type": "sections"},
                    {"path": "settings", "type": "panel"}
                ]
            }"""
        )
        val view = SiftJson.extractViewJson(lovelaceJson, "home")
        assertEquals("home", view?.getString("path"))
        assertEquals("sections", view?.getString("type"))
    }

    @Test
    fun extractViewJson_returnsNullWhenNotFound() {
        val lovelaceJson = JSONObject("""{
                                            "views": []
                                            }""")
        val view = SiftJson.extractViewJson(lovelaceJson, "missing")
        assertNull(view)
    }
}