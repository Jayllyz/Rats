package com.rats.data.dto

import com.rats.models.Report

data class LineInformationDTO(
    val id: Int,
    val name: String,
    val status: String,
    val reports: List<Report>
)
