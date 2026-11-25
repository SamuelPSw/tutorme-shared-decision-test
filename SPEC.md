# TutorMe Product Specification

## Overview

TutorMe is a web application that allows tutors to
manage lesson notes and share them with families.

## Tutor Dashboard

- Create and upload text-based lesson notes.
- Associate notes with a specific family.
- View previously uploaded notes.
- Review and approve lesson notes.
- Display the approval status of each note.

## Family Dashboard

- View lesson notes associated with the family.
- Display the lesson title, content and creation date.
- Prevent access to notes belonging to other families.

## Data Model

Each lesson note contains:

- id
- familyId
- title
- content
- approved (boolean)
- createdAt

## Technical Requirements

- Backend: Java Spring Boot
- Frontend: React and Vite
- Storage: H2 in-memory database
- Backend port: 8080
- Frontend port: 5173
- Communication through a REST API

## Scope

This is a functional local demonstration.

Real authentication, payments, scheduling and PDF
storage are outside the scope of this application.

Use two predefined demo families with IDs 1 and 2.

Follow the shared API contract in API_CONTRACT.md.
