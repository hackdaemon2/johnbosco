package com.interswittch.johnbosco.common.dto;

public record CustomApiResponse<T>(T data, boolean error) {
}
