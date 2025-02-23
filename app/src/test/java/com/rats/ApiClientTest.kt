package com.rats

import com.rats.utils.ApiClient
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ApiClientTest {
    private lateinit var mockOkHttpClient: OkHttpClient
    private lateinit var mockCall: okhttp3.Call
    private lateinit var mockResponse: Response
    private lateinit var mockResponseBody: ResponseBody
    private lateinit var mockRequestBody: JSONObject

    @Before
    fun setup() {
        mockOkHttpClient = mockk(relaxed = true)
        mockCall = mockk(relaxed = true)
        mockResponse = mockk(relaxed = true)
        mockResponseBody = mockk(relaxed = true)
        mockRequestBody = mockk(relaxed = true)

        ApiClient.setClient(mockOkHttpClient)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getRequest successful response`() =
        runTest {
            val url = "test-endpoint"
            val expectedJsonString = """{"key":"value"}"""
            val expectedJson = Json.parseToJsonElement(expectedJsonString)

            every { mockResponseBody.string() } returns expectedJsonString
            every { mockResponse.body } returns mockResponseBody
            every { mockResponse.isSuccessful } returns true
            every { mockResponse.code } returns 200
            every { mockCall.execute() } returns mockResponse
            every { mockOkHttpClient.newCall(any()) } returns mockCall

            val result = ApiClient.getRequest(url)

            assertEquals(200, result.code)
            assertEquals(expectedJson, result.body)
            verify { mockOkHttpClient.newCall(any()) }
        }

    @Test
    fun `getRequest with authorization header`() =
        runTest {
            val url = "test-endpoint"
            val token = "test-token"
            val expectedJsonString = """{"key":"value"}"""

            every { mockResponseBody.string() } returns expectedJsonString
            every { mockResponse.body } returns mockResponseBody
            every { mockResponse.isSuccessful } returns true
            every { mockResponse.code } returns 200
            every { mockCall.execute() } returns mockResponse
            every { mockOkHttpClient.newCall(any()) } returns mockCall

            val result = ApiClient.getRequest(url, token)

            assertEquals(200, result.code)
            verify {
                mockOkHttpClient.newCall(
                    match { request ->
                        request.header("Authorization") == "Bearer $token"
                    },
                )
            }
        }

    @Test
    fun `postRequest successful response`() =
        runTest {
            val url = "test-endpoint"
            val expectedJsonString = """{"status":"success"}"""
            val expectedJson = Json.parseToJsonElement(expectedJsonString)

            every { mockResponseBody.string() } returns expectedJsonString
            every { mockResponse.body } returns mockResponseBody
            every { mockResponse.isSuccessful } returns true
            every { mockResponse.code } returns 200
            every { mockCall.execute() } returns mockResponse
            every { mockOkHttpClient.newCall(any()) } returns mockCall
            every { mockRequestBody.toString() } returns """{"key":"value"}"""

            val result = ApiClient.postRequest(url, mockRequestBody)

            assertEquals(200, result.code)
            assertEquals(expectedJson, result.body)
            verify {
                mockOkHttpClient.newCall(
                    match { request ->
                        request.method == "POST" &&
                            request.header("Content-Type") == "application/json"
                    },
                )
            }
        }

    @Test
    fun `postRequest with authorization header`() =
        runTest {
            val url = "test-endpoint"
            val token = "test-token"
            val expectedJsonString = """{"status":"success"}"""

            every { mockResponseBody.string() } returns expectedJsonString
            every { mockResponse.body } returns mockResponseBody
            every { mockResponse.isSuccessful } returns true
            every { mockResponse.code } returns 200
            every { mockCall.execute() } returns mockResponse
            every { mockOkHttpClient.newCall(any()) } returns mockCall
            every { mockRequestBody.toString() } returns """{"key":"value"}"""

            val result = ApiClient.postRequest(url, mockRequestBody, token)

            assertEquals(200, result.code)
            verify {
                mockOkHttpClient.newCall(
                    match { request ->
                        request.method == "POST" &&
                            request.header("Authorization") == "Bearer $token" &&
                            request.header("Content-Type") == "application/json"
                    },
                )
            }
        }

    @Test
    fun `putRequest successful response`() =
        runTest {
            val url = "test-endpoint"
            val expectedJsonString = """{"status":"updated"}"""
            val expectedJson = Json.parseToJsonElement(expectedJsonString)

            every { mockResponseBody.string() } returns expectedJsonString
            every { mockResponse.body } returns mockResponseBody
            every { mockResponse.isSuccessful } returns true
            every { mockResponse.code } returns 200
            every { mockCall.execute() } returns mockResponse
            every { mockOkHttpClient.newCall(any()) } returns mockCall
            every { mockRequestBody.toString() } returns """{"key":"value"}"""

            val result = ApiClient.putRequest(url, mockRequestBody)

            assertEquals(200, result.code)
            assertEquals(expectedJson, result.body)
            verify {
                mockOkHttpClient.newCall(
                    match { request ->
                        request.method == "PUT" &&
                            request.header("Content-Type") == "application/json"
                    },
                )
            }
        }

    @Test
    fun `putRequest with authorization header`() =
        runTest {
            val url = "test-endpoint"
            val token = "test-token"
            val expectedJsonString = """{"status":"updated"}"""

            every { mockResponseBody.string() } returns expectedJsonString
            every { mockResponse.body } returns mockResponseBody
            every { mockResponse.isSuccessful } returns true
            every { mockResponse.code } returns 200
            every { mockCall.execute() } returns mockResponse
            every { mockOkHttpClient.newCall(any()) } returns mockCall
            every { mockRequestBody.toString() } returns """{"key":"value"}"""

            val result = ApiClient.putRequest(url, mockRequestBody, token)

            assertEquals(200, result.code)
            verify {
                mockOkHttpClient.newCall(
                    match { request ->
                        request.method == "PUT" &&
                            request.header("Authorization") == "Bearer $token" &&
                            request.header("Content-Type") == "application/json"
                    },
                )
            }
        }
}
