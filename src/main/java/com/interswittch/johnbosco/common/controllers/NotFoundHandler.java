package com.interswittch.johnbosco.common.controllers;

import com.interswittch.johnbosco.common.dto.CustomApiResponse;
import com.interswittch.johnbosco.common.dto.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/errors")
public class NotFoundHandler {

    @GetMapping
    public ResponseEntity<CustomApiResponse<ErrorResponseDto>> handlePathNotFound() {
        CustomApiResponse<ErrorResponseDto> response = new CustomApiResponse<>(new ErrorResponseDto("resource not found"), true);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
