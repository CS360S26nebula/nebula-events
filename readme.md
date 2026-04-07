# Campus Gate Access System

## Table of Contents
- [Team Information](#team-information)
-[Latest deliverable milestones](#Halfway-Deliverable:-Project-Features)
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
# Halfway Deliverable: Project Features

### Administrator Controls
* **Manual Gate Adjustments**: Admins can easily change gate opening and closing times or shut the gate to visitors for the day when needed.
* **User Management**: A straightforward dashboard for Admins to add new users, remove old ones, or manage the blacklist directly through the app.

### Visitor Access & Scanning
* **Offline QR Scanning**: Visitors can download their approved QR code to their phone gallery. Guards can scan these codes even if the visitor doesn't have the app or a working internet connection.
* **Direct Guest Entry**: Guards can register guest entries for students on the spot. This removes the need for secondary approvals and prevents delays at the gate during busy hours.

### Data Reliability
* **Real-Time Synchronization**: All account data and entry logs are synced to a live database, keeping everything consistent across the Admin, Guard, and Faculty views.
* **Searchable Logs**: Entry history is kept safe and organized, making it easy to search and query whenever you need to look up a specific record.
---

## Meeting Minutes
##Phase-2

---
### Meeting – March 30, 2026

#### Date
Monday, March 30, 2026
Duration: 30 minutes
---

#### Key Takeaways
- decided work timeline for the deliverable.
- divided user stories and workload
- started converting Figma UI to android xmls.
---
### Meeting – April 6, 2026

#### Date
Monday, April 6, 2026
Duration: 2 hours 30 minutes
---

#### Key Takeaways
- Group coding/ brain storming session.
- Ensured consistent usage of colour schemes, constants across different files.
- Fixed recurring issues with test cases from last few commits.
- merged pull requests with code conflicts.
---

---
### Meeting – April 7, 2026

#### Date
Tuesday, April 7, 2026
Duration: 2 hours
---

#### Key Takeaways
- Coding, bug fixes and discussion over final group deliverables.
- Meeting with TA to discuss app improvements and final refinements.
- Last minute merge conflicts resolution.
- division of work for formatting, uploads and code integration.

---


##Phase-1
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

Product Backlog is also available in the kanban board linked at the end of this readme file. 
### Product Backlog – Project Part 1

| ID | Theme | User Story | Risk | Points | Release |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **US01** | Auth | As a security administrator, I want to create and manage accounts for security guards, so only authorized personnel can access the gate dashboard. | Low | 2 | Half-way |
| **US02** | Auth | As a security guard, I want to log securely into the mobile/tablet application, so my specific actions and logs are tied to my identity. | Low | 3 | Half-way |
| **US03** | Pre-Plan | As a faculty/staff member, I want to submit a visitor request with dates, times, license plates, and a CNIC upload, so my guests are pre-approved for entry. | Low | 3 | Half-way |
| **US04** | Dashboard | As a security guard, I want to view a daily, filterable dashboard of all expected visitors, so I know exactly who is authorized to arrive today. | Medium | 5 | Half-way |
| **US05** | Verification | As a security guard, I want to search for a visitor by name, CNIC, or host name, so that I can manually verify them if they lose their digital pass. | Low | 2 | Half-way |
| **US06** | Logging | As a security guard, I want to log the exact entry time of a verified visitor, so that the system updates the campus occupancy record. | Medium | 3 | Half-way |
| **US07** | Logging | As a security guard, I want to log the exit time of a visitor as they leave, so that the trail reflects that they are no longer on campus. | Low | 2 | Half-way |
| **US08** | Ad-Hoc | As a security guard, I want to trigger an ad-hoc approval notification to a specific host (Student/Faculty), so that I can process unexpected arrivals. | High | 5 | Final |
| **US09** | Ad-Hoc | As a host (Faculty/Student), I want to receive real-time notifications to approve or deny an unexpected guest, so that I maintain control over who visits me. | High | 8 | Final |
| **US10** | Exception | As a security guard, I want to be able to log an "Emergency" entry (ambulances), so that critical personnel can enter immediately while still leaving an audit trail. | Medium | 3 | Final |
| **US11** | Audit | As a security administrator, I want to generate filterable reports of all entries, exits, and ad-hoc overrides, so that I can audit campus security procedures. | Medium | 5 | Final |
| **US12** | Exception | As a security administrator, I want to suspend or resume visitor entries for any duration, so that campus access can be restricted on certain events. | Low | 2 | Half-way |
| **US13** | Verification | As a security guard, I want to use the device camera to scan a visitor's QR code digital pass, so that I can instantly verify their pre-approved status. | Medium | 3 | Half-way |
| **US14** | Security | As a security administrator, I want to add or remove specific individuals from a blacklist using their CNIC, so that offenders are blocked from campus entry. | Medium | 3 | Final |
| **US15** | Config | As a security administrator, I want to configure the standard daily gate timings (open/close hours), so that the system automatically enforces regular campus hours. | Low | 3 | Final |
| **US16** | Config | As a security administrator, I want to toggle specific feature permissions for different user roles, so that I can dynamically control system access. | High | 5 | Final |

---

### Product Backlog – Project Part 2
| ID | Theme | User Story | Risk | Points | Release |
| :--- | :--- | :--- | :--- | :--- | :--- |

### Product Backlog – Project Part 3
| ID | Theme | User Story | Risk | Points | Release |
| :--- | :--- | :--- | :--- | :--- | :--- |

---

### Project Backlog
[Link for Github KanBan Board](https://github.com/orgs/CS360S26nebula/projects/3/views/1)

## Wireframes

### Wireframes – Project Part 1

### Links

### Figma StoryBoard
[Link to storyboard](https://www.figma.com/design/9Me5uaUCRX8WqCouXx7nPc/CS-360-Project-Part-2-Storyboard?node-id=137-1691&t=9reMOKxj25ZCYS2n-1)

### Screenshots

### StoryBoard Screenshots

### Without Arrows
<img width="1103" height="877" alt="storyboard" src="https://github.com/user-attachments/assets/9577adf1-761a-4b68-8684-d9d22513988d" />

### With Arrows
<img width="1117" height="905" alt="storyboardarrows" src="https://github.com/user-attachments/assets/7563ca0d-195e-463b-bbeb-40d93d802d35" />

### Wireframes – Project Part 2
_Add screenshots or links to wireframe images._

### Wireframes – Project Part 3
_Add screenshots or links to wireframe images._












