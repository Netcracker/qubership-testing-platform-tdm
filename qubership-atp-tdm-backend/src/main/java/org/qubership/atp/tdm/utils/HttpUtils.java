/*
 * # Copyright 2024-2025 NetCracker Technology Corporation
 * #
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * # you may not use this file except in compliance with the License.
 * # You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing, software
 * # distributed under the License is distributed on an "AS IS" BASIS,
 * # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * # See the License for the specific language governing permissions and
 * # limitations under the License.
 */

package org.qubership.atp.tdm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriUtils;

public class HttpUtils {

    /**
     * Building response entity containing file for download to user.
     *
     * @param file        - formed file.
     * @param contentType - type of file.
     * @return - response entity.
     * @throws FileNotFoundException -if there is no such file.
     */
    public static ResponseEntity<InputStreamResource> buildFileResponseEntity(File file, String contentType)
            throws FileNotFoundException {

        if (file == null || !file.isFile()) {
            throw new FileNotFoundException(file == null ? "null" : file.getPath());
        }

        String filename = file.getName();
        String contentDisposition = contentDisposition(filename);
        ResponseEntity<InputStreamResource> body = ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .header("Content-Disposition", contentDisposition)
                .header("Content-Type", contentType)
                .body(new InputStreamResource(new FileInputStream(file)));
        DataUtils.deleteFile(file.toPath());
        return body;
    }

    private static String contentDisposition(String filename) {
        String quoted = filename.replace("\"", "\\\"");
        String utf8 = UriUtils.encode(filename, StandardCharsets.UTF_8);
        return "attachment; filename=\"" + quoted + "\"; filename*=UTF-8''" + utf8;
    }
}
