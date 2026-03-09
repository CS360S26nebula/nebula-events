# Campus Gate Access System

## Table of Contents
- [Team Information](#team-information)

- [Meeting Minutes](#meeting-minutes)
  - [Meeting – Feb 17, 2026](#meeting--feb-17-2026)
  - [Meeting – Feb 22, 2026](#meeting--feb-22-2026)
  - [Meeting – Mar 6, 2026](#meeting--Mar-6-2026)
  - [Meeting – TBD](#meeting--tbd)

- [UML Diagrams](#uml-diagrams)

- [Product Backlog](#product-backlog)
  - [Product Backlog – Project Part 1](#product-backlog--project-part-1)
  - [Product Backlog – Project Part 2](#product-backlog--project-part-2)
  - [Product Backlog – Project Part 3](#product-backlog--project-part-3)

- [Links](#Links)

---

## Team Information
- **Team Name:** _Nebula_

| Name               | Roll Number | GitHub ID                 |
|--------------------|-------------|---------------------------|
| Abdullah Ahmad     | 27100387    | abdullah-ahmad-915-nlp    |
| Ali Azhar          | 27100083    | Ali-5987                  |
| Moiz Imran         | 26100421    | MoizImran01               |
| Muhammad Yahya Ali | 27100160    | MYA-1635                  |
| Umer Ashraf        | 27100236    | UmerAshraf-236            |

---

## Meeting Minutes
### Meeting – Feb 17, 2026

#### Date
Friday, February 17, 2026
Duration: 45 minutes
#### Attendance
- All Present 

---

#### Key Takeaways
- Discussion on topic area for the project.
- Went over each topic's scope and application.
- Created common github organization and repository.
- Added initial commits, and resolved conflicts in pull requests.
---


### Meeting – Feb 22, 2026

#### Date
Friday, February 22, 2026
Duration: 25 minutes
#### Attendance
- All Present 

---

#### Key Takeaways
- Deliverables review and allignment with expected submission criterias.
- Usage of Figma, scope of UML diagrams and expectations from submissions in phase 2.
- Addressed points of confusion about project timeline, start point and initial progress.
- Resolved queries about design choices and project scope. 
- Storyboard requirements:
---
### Meeting – Mar 6, 2026

#### Date
Friday, March 6, 2026
Duration: 1 hour 15 minutes
#### Attendance
- All Present 

---

#### Key Takeaways
- Discussed workload divison, assigned tasks for Figma and CRC analysis
- Decided figma colour scheme
- Final classes and features discussed
- Storyboard requirements:
---

#### CRC Analysis
### CRC Cards Analysis

| Class Name | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **Faculty** | • Create an expected-visitor request before the guest arrives (CNIC + phone number).<br>• Receive ad-hoc visitor requests (when a guest shows up unexpectedly).<br>• Accept or reject an ad-hoc request.<br>• Provide basic host info needed for verification. | • Administration<br>• Security<br>• Visitor<br>• Logs<br>• Request |

---

| Class Name | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **Administration** | • Create login credentials for Faculty and Security (by default).<br>• Control access rules (who can view/edit what).<br>• Maintain the blacklist (since it already exists).<br>• View all requests and logs (full system visibility). | • Administration<br>• Security<br>• Visitor<br>• Logs<br>• Request |

---

| Class Name | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **Security** | • Verify visitors at the gate using CNIC + phone.<br>• Check blacklist status before allowing entry.<br>• For expected visitors: find the matching request and proceed.<br>• For ad-hoc visitors: create a new request and send it to Faculty for approval.<br>• Create gate visit logs for time-in (entry) and time-out (exit).<br>• Follow access rules (security can log visits but cannot edit old records). | • Administration<br>• Security<br>• Visitor<br>• Logs<br>• Request |

---

| Class Name | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **Visitor** | • Store visitor identifiers (CNIC + phone number).<br>• Be searchable by CNIC/phone/plate for gate verification.<br>• Link to a Request (expected or ad-hoc) or a Log entry (time-in/time-out record). | • Administration<br>• Security<br>• Visitor<br>• Logs<br>• Request |

---

| Class Name | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **Logs** | • Store gate activity (time-in, time-out, which security person logged it).<br>• Store key approval outcomes (especially ad-hoc accept/reject result).<br>• Provide admin a reliable record of what happened (who entered, who left, when). | • Administration<br>• Security<br>• Visitor<br>• Logs<br>• Request |

---

| Class Name | Responsibilities | Collaborators |
| :--- | :--- | :--- |
| **Request** | • Store request details (visitor CNIC + phone number, host name, request type: expected or ad-hoc).<br>• For ad-hoc requests, set status as pending, send to faculty for accept/reject and then store the final decision.<br>• Act as the source of truth for whether a visitor is allowed to enter. | • Administration<br>• Security<br>• Visitor<br>• Logs<br>• Request |

---

#### General Notes
- Maintain consistent formatting for user stories and storyboards.
- Submit **one main storyboard** (high-level).
- Storyboard does not need to be visually polished.
- Supplementary videos and feature storyboards are allowed.
- Presentation:
  - 10 minutes per group
  - Dataset generated by team
- No strict requirements — apply reasonable design judgment.


---

## UML Diagrams
_Will be added in next phase_

---

## Product Backlog

### Product Backlog – Project Part 1
| ID | User Story | Priority | Status |
|----|------------|----------|--------|

### Product Backlog – Project Part 2
| ID | User Story | Priority | Status |
|----|------------|----------|--------|

### Product Backlog – Project Part 3
| ID | User Story | Priority | Status |
|----|------------|----------|--------|

---

### Links

### Figma StoryBoard
[Link to storyboard](https://www.figma.com/design/9Me5uaUCRX8WqCouXx7nPc/CS-360-Project-Part-2-Storyboard)

### Project Backlog
[Link for Github KanBan Board](https://github.com/orgs/CS360S26nebula/projects/3/views/1)








