# TutorMe Product Requirements

TutorMe is a platform that allows tutors to manage
tutoring sessions and share lesson notes with families.

## Requirements

- Tutors can upload lesson notes after a tutoring session.
- Tutors can review and approve uploaded lesson notes.
- Tutors can send payment reminders. 
- Each lesson note belongs to a specific family.
- Families can view their lesson notes through a dashboard.
- Families cannot access notes belonging to other families.
- Families can receive payment reminders to their email. 

## Data Model

Each lesson note contains:

- id
- familyId
- content
- approved (boolean)

## Architecture

The backend manages lesson-note storage, approval
and retrieval.

The frontend determines which lesson notes are
displayed to a family from a supplied list of notes.

Existing function signatures must be maintained.
