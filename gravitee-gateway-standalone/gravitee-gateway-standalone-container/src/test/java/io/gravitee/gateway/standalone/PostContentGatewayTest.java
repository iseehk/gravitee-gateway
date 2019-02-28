/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.gateway.standalone;

import io.gravitee.common.http.HttpHeadersValues;
import io.gravitee.common.http.MediaType;
import io.gravitee.gateway.standalone.junit.annotation.ApiDescriptor;
import io.gravitee.gateway.standalone.utils.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
@ApiDescriptor("/io/gravitee/gateway/standalone/teams.json")
public class PostContentGatewayTest extends AbstractGatewayTest {

    @Test
    public void shouldReturnOk_postContent() throws Exception {
        String mockContent = StringUtils.copy(
                getClass().getClassLoader().getResourceAsStream("case1/response_content.json"));

        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()
                                .withBody(mockContent)
                                .withHeader(io.gravitee.common.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        Request request = Request.Post("http://localhost:8082/test/my_team")
                .bodyStream(
                        this.getClass().getClassLoader().getResourceAsStream("case1/request_content.json"),
                        ContentType.APPLICATION_JSON);
        HttpResponse response = request.execute().returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseContent = StringUtils.copy(response.getEntity().getContent());

        assertEquals(mockContent, responseContent);

        verify(postRequestedFor(urlEqualTo("/team/my_team"))
                .withRequestBody(equalToJson(mockContent)));
    }

    @Test
    public void call_case1_raw() throws Exception {
        String mockContent = StringUtils.copy(
                getClass().getClassLoader().getResourceAsStream("case1/response_content.json"));

        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()
                                .withBody(mockContent)
                                .withHeader(io.gravitee.common.http.HttpHeaders.CONTENT_LENGTH, Integer.toString(mockContent.length()))
                                .withHeader(io.gravitee.common.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        Request request = Request.Post("http://localhost:8082/test/my_team")
                .bodyStream(
                        this.getClass().getClassLoader().getResourceAsStream("case1/request_content.json"),
                        ContentType.APPLICATION_JSON);
        HttpResponse response = request.execute().returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseContent = StringUtils.copy(response.getEntity().getContent());

        assertEquals(mockContent, responseContent);
        assertEquals(mockContent.length(), Integer.parseInt(response.getFirstHeader(HttpHeaders.CONTENT_LENGTH).getValue()));
        assertEquals(responseContent.length(), Integer.parseInt(response.getFirstHeader(HttpHeaders.CONTENT_LENGTH).getValue()));

        verify(postRequestedFor(urlEqualTo("/team/my_team"))
                .withRequestBody(equalToJson(mockContent)));
    }

    @Test
    public void call_case1_chunked_request() throws Exception {
        String mockContent = StringUtils.copy(
                getClass().getClassLoader().getResourceAsStream("case1/response_content.json"));

        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()
                                .withBody(mockContent)
                                .withHeader(io.gravitee.common.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        Request request = Request.Post("http://localhost:8082/test/my_team")
                .bodyStream(
                        this.getClass().getClassLoader().getResourceAsStream("case1/request_content.json"),
                        ContentType.APPLICATION_JSON);
        HttpResponse response = request.execute().returnResponse();

        String responseContent = StringUtils.copy(response.getEntity().getContent());

        assertEquals(mockContent, responseContent);
        assertNull(response.getFirstHeader(HttpHeaders.CONTENT_LENGTH));
        assertNotNull(response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING));
        assertEquals(HttpHeadersValues.TRANSFER_ENCODING_CHUNKED, response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING).getValue());

        verify(postRequestedFor(urlEqualTo("/team/my_team"))
                .withRequestBody(equalToJson(mockContent)));
    }

    @Test
    public void call_case2_chunked() throws Exception {
        String mockContent = StringUtils.copy(
                getClass().getClassLoader().getResourceAsStream("case2/response_content.json"));

        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()
                                .withBody(mockContent)
                                .withHeader(io.gravitee.common.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        Request request = Request.Post("http://localhost:8082/test/my_team")
                .bodyString(
                        mockContent,
                        ContentType.APPLICATION_JSON);

        HttpResponse response = request.execute().returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String content = StringUtils.copy(response.getEntity().getContent());
        assertEquals(652051, content.length());
        assertEquals(content, content);
        assertNull(response.getFirstHeader(HttpHeaders.CONTENT_LENGTH));
        assertNotNull(response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING));
        assertEquals(HttpHeadersValues.TRANSFER_ENCODING_CHUNKED, response.getFirstHeader(HttpHeaders.TRANSFER_ENCODING).getValue());

        verify(postRequestedFor(urlEqualTo("/team/my_team"))
                .withRequestBody(equalTo(mockContent)));
    }

    @Test
    public void call_case3_raw() throws Exception {
        String mockContent = StringUtils.copy(
                getClass().getClassLoader().getResourceAsStream("case3/response_content.json"));

        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()
                                .withBody(mockContent)
                                .withHeader(io.gravitee.common.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)));

        Request request = Request.Post("http://localhost:8082/test/my_team")
                .bodyStream(
                        getClass().getClassLoader().getResourceAsStream("case3/request_content.json"),
                        ContentType.APPLICATION_JSON);

        HttpResponse response = request.execute().returnResponse();

        String responseContent = StringUtils.copy(response.getEntity().getContent());
        assertEquals(70, responseContent.length());

        verify(postRequestedFor(urlEqualTo("/team/my_team"))
                .withRequestBody(equalToJson(mockContent)));
    }

    @Test
    public void call_post_no_content_with_chunked_encoding_transfer() throws Exception {
        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()));

        Request request = Request.Post("http://localhost:8082/test/my_team");

        HttpResponse response = request.execute().returnResponse();

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        // Set chunk mode in request but returns raw because of the size of the content
        assertEquals(null, response.getFirstHeader("X-Forwarded-Transfer-Encoding"));

        String responseContent = StringUtils.copy(response.getEntity().getContent());
        assertEquals(0, responseContent.length());

        verify(postRequestedFor(urlEqualTo("/team/my_team")));
    }

    @Test
    public void call_post_no_content_without_chunked_encoding_transfer() throws Exception {
        stubFor(post(urlEqualTo("/team/my_team"))
                .willReturn(
                        ok()));

        Request request = Request.Post("http://localhost:8082/test/my_team")
                .removeHeaders(HttpHeaders.TRANSFER_ENCODING);

        Response response = request.execute();

        HttpResponse returnResponse = response.returnResponse();
        assertEquals(HttpStatus.SC_OK, returnResponse.getStatusLine().getStatusCode());

        // Set chunk mode in request but returns raw because of the size of the content
        assertEquals(null, returnResponse.getFirstHeader("X-Forwarded-Transfer-Encoding"));

        String responseContent = StringUtils.copy(returnResponse.getEntity().getContent());
        assertEquals(0, responseContent.length());

        verify(postRequestedFor(urlEqualTo("/team/my_team")));
    }
}
