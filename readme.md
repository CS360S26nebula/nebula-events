# Campus Gate Access System

## Table of Contents
- [Team Information](#team-information)
- [Latest deliverable milestones](#halfway-deliverable-project-features)
- [Meeting Minutes](#meeting-minutes)
  - [Meeting – March 30, 2026](#meeting--march-30-2026)
  - [Meeting – April 6, 2026](#meeting--april-6-2026)
  - [Meeting – April 7, 2026](#meeting--april-7-2026)
  - [Meeting – Feb 17, 2026](#meeting--feb-17-2026)
  - [Meeting – Feb 22, 2026](#meeting--feb-22-2026)
  - [Meeting – Mar 6, 2026](#meeting--mar-6-2026)
- [UML Diagrams](#uml-diagrams)
- [Product Backlog](#product-backlog)
  - [Product Backlog – Project Part 1](#product-backlog--project-part-1)
  - [Product Backlog – Project Part 2](#product-backlog--project-part-2)
  - [Product Backlog – Project Part 3](#product-backlog--project-part-3)
- [Links](#links)


---
## Usage

* **Quick Installation**: Download the application from the `APK` folder for a one-click setup on your Android device.
* **Manual Compilation**: Alternatively, you can build the project yourself using the source files found in the `project-application` directory.
---

## Team Information
- **Team Name:** _Nebula_

| Name | Roll Number | GitHub ID |
| :--- | :--- | :--- |
| Abdullah Ahmad | 27100387 | abdullah-ahmad-915-nlp |
| Ali Azhar | 27100083 | Ali-5987 |
| Moiz Imran | 26100421 | MoizImran01 |
| Muhammad Yahya Ali | 27100160 | MYA-1635 |
| Umer Ashraf | 27100236 | UmerAshraf-236 |

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

### Faculty Pre-Approval System
* **Advance Guest Registration**: Faculty can log visitors before they arrive, ensuring all security details are handled in advance. Any status update—whether a request is approved or declined—shows up on your personal dashboard in real-time.
* **Direct Pass Sharing**: Once a request is cleared, you can save the digital pass and send it straight to your visitor. This ensures a **hassle-free** arrival, as your guest only needs to show the code at the gate to be scanned in, even if they don't have the app themselves.
---

## Meeting Minutes

### Phase-2

---

### Meeting – March 30, 2026
**Date:** Monday, March 30, 2026  
**Duration:** 30 minutes

#### Key Takeaways
- decided work timeline for the deliverable.
- divided user stories and workload
- started converting Figma UI to android xmls.

---

### Meeting – April 6, 2026
**Date:** Monday, April 6, 2026  
**Duration:** 2 hours 30 minutes

#### Key Takeaways
- Group coding/ brain storming session.
- Ensured consistent usage of colour schemes, constants across different files.
- Fixed recurring issues with test cases from last few commits.
- merged pull requests with code conflicts.

---

### Meeting – April 7, 2026
**Date:** Tuesday, April 7, 2026  
**Duration:** 2 hours

#### Key Takeaways
- Coding, bug fixes and discussion over final group deliverables.
- Meeting with TA to discuss app improvements and final refinements.
- Last minute merge conflicts resolution.
- division of work for formatting, uploads and code integration.

---

### Phase-1

---

### Meeting – Feb 17, 2026
**Date:** Friday, February 17, 2026  
**Duration:** 45 minutes  
**Attendance:** All Present  

#### Key Takeaways
- Discussion on topic area for the project.
- Went over each topic's scope and application.
- Created common github organization and repository.
- Added initial commits, and resolved conflicts in pull requests.

---

### Meeting – Feb 22, 2026
**Date:** Friday, February 22, 2026  
**Duration:** 25 minutes  
**Attendance:** All Present  

#### Key Takeaways
- Deliverables review and allignment with expected submission criterias.
- Usage of Figma, scope of UML diagrams and expectations from submissions in phase 2.
- Addressed points of confusion about project timeline, start point and initial progress.
- Resolved queries about design choices and project scope. 
- Storyboard requirements:

---

### Meeting – Mar 6, 2026
**Date:** Friday, March 6, 2026  
**Duration:** 1 hour 15 minutes  
**Attendance:** All Present  

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

## UML Diagrams
[UML Admin Module](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&dark=auto#R%3Cmxfile%3E%3Cdiagram%20name%3D%22Seite-1%22%20id%3D%22gWttRObwHHih-HlRBOAE%22%3E7V1bU%2BO4Ev41VGUf2IrvyWNIwiy1MMMBZnbnaUqJRaLCtry2w2V%2B%2FZF8i6%2FgGCtYRltbNUSW5Far%2B1N3qyWfKHP7%2BYsH3O0VNqF1Io%2FN5xNlcSLL8sTQyT%2B05CUqmShyVLDxkBkVSfuCW%2FQbxoXjuHSHTOjnKgYYWwFy84Vr7DhwHeTKgOfhp3y1e2zl3%2BqCDSwV3K6BVS79B5nBNh6FbOzL%2F4Jos03eLOnT6MkKrB82Ht458fsc7MDoiQ2SbuIx%2Bltg4qdMkbI8UeYexkH0l%2F08hxZla8KxqN15zdOUZA86QZMGSrlB3IcfvCSM2Aa2Rf6STpSze2RZc2xhL3yiQMnUoEHK%2FcDDDzDzZKobCtDJk0foBYgwdWahjUOeBdglpSD%2BZcF78tozTGrdWyEj6Bvoi7ATnAMbWVR4fkDPBA4gxU9bFMBbF6wpaU9E7khZRO8jsHYxvSeybtFe3dww9P92lKtnNvA2iLx7Nnaf05%2BnIVkzNSwK4HNwGlM4WxPGQC98TdID%2BWsT%2Fxu%2BZ5UUzEwbOX9hG87WAXpEwUtSg0zAqtiKlLnFsq1HSU40IXmfVP%2FqQ4YY8bpqjPEslF9zSrqff7v8dvNrNr%2B7%2BLEkP0l1Kj9EWpLRe%2BXaF1%2Bb1nfAI2XYBVHitPKFTVTrB4JPtU2IljoHNrkGvg%2F9Qxt5mMjjodTRAd0R%2FqZN6I83x3NYi2g4B7aJRvNqIx6k8kQm%2F4%2BxM%2FcgCODIB4%2FQvHD8ADhreBuQonR0ZwSDLfhH%2BvsRE%2BArsSfp7wb6OxuO3qpOuQmogpM33YHVKACrrJxXN8%2FzNQQsAozwOYO5MUJ%2FgUR8Ao9gx3ibWV4UTY3aPe3XImUyicqe0yUoXpziJVcbx%2BsMiNfCTdr5flEgf8TrQvUaoYo1ous14go6u7%2BwHwxlnajSKZ6oZ4EnBHDRhkIEHt17YGNT5UnanacFc6qcpnmHz4jRSF62fkgrrYixC4HDCE8ko4wnmprHE2UsKd3jiSbwhAWe7GVKYMlHY0lpUqhRUWd2RLBDjbARcu4tQKc%2BUflL8IJ3wUVaPI%2F83QAgJ1OLtv1CHE43qnAAgFXbiwll9GlEnTl6DGvmmh34rm7hayqX4GuSeNVJN6qi5%2BBLnXQAX7qArw7h64oQuYHffej5odIIFOsH9RQA6ubmNTDrM2RUWTxJTPI5hRUtBxmK0QFkGAIyOrV4TCqTg4ILGlO4Xc6%2FfV3Mbn7%2Bml1f%2F%2Fo6u9qHz24DDzmb6lDEzfJ%2F35e3d7%2F%2BXpKGi8Wv77fLm1%2Bkp%2FOLm6tGHZx9%2F7q4XIbt42bLRaOG0ETBV2Dv1XdJCqLoUl31pQ2QdUB9GuZ6wp55QJM5du6RZ7doeb0NA%2FXN3%2BSgdcPqvntDnLk9V13khAZcdd1FFhPfqHtFVHjbtPJPCLxmdVeBE%2BtZWv%2BKmqAIWGe7IKDx0KpmhPPQD7C3J%2F%2BclKyAD8%2F3T6oarsM1I9TrXWY8SeuokB99TtyAMlJ9zOKZZzZyUBj09ctvadIcu9CJtQwECDvX2N25jcKm%2Fm5loyBmS6MWZMFBJhnlhePugtH9zrJyiJPAE2WNl9OwzAMzp06ZB3ZedzJPXnKKknkAcwCWfeIWASf7cF0DSrkOcvCTa50FmujBnndxmOg1taLaM3PMfzwUqVhbPuJV8rM7zrQcdb3EPCWjXOD1LtS4Hap%2B9ZG50PVA13Tady6d3HBSy8NspJIwuMTAJLVHVvRvUbKa9WLBNYGtH9Q%2BHPnJypJfaf4ojb6qqwADPxjZ0Pfp%2FvzhA1rtkGXeQqJxJvBeKHcyQFO1nhSaI8eM6a0bRzRrRMpsvzgi7exEWzSm8gY4GzjaQlAtnwTkvX3MGCXhYuiY2cISV0MaOvOadGNS8pokbarn3KZTUjLtPlI8EX5T935TduUenA91TGeIB57k7NDameczOn1L4fFNI5KDwFQKdK%2FEshXFkPOBKbUDgJ0KgO0QYG%2BgTUYyyNhUFldvllfffiz7HWdiFJpZBc5%2BktMmHQdGeJCJCFVrBH4AIY4Dwg4V3lutB9nQa39vgGU%2FL40amcR5i1zms5cFJKu35bMY8qs%2BaETysDxR6qB7oQT5NfPwbtNBVct7WrpScM6MJKM%2BMR2mHZgOSZ%2FCdujYdvg0%2FtkR7Age2FJcSoWXxq%2BXpo4nUj7jqIv0AaniWI%2BA2tZQe2aB9cMl8oPBe2pnl7P535cX5Ndx%2FLSDvLRDfDRqHwI%2Fcwiny932EKEO2XIPGxy07x62aL75Hje4Q%2FZhNM3sa7tpg6VjHjJmUv2gEZP6h4yXVD9ktKR687ESpz1UeYuofNri0%2Frs9eg3ALf9vY5zKictchMa%2BvbIvyYvgVtsmfU7jA37IgaBD6kWU925Iksy8kcmeCmBdctchwDV%2BP%2FAdu3Cgz3FFq5bH4ibnzK4X55%2B6KmfY89uNO%2Fv3qEWW8z5LeZkNzlrWSeBjSSIIasMjiJJsrCsWVnWnyaOwdzK5oEpFcaFCGRwHMgw1DEDuBXXjXQbMyZ8QOtg6ZB5GBTEVukVT9RHEd262emDoyVs0AIoJvtZrxwOk9WpygAUxf0anZ%2BH%2FxL5xERM%2FKFAItWmLfC%2FO6H6z7dUp%2FZ6%2BWqY4AnCB%2Bsl5sfemgrDYTrx5Um1sNUCkKnOsC59WoPJYe4ACACNiKXdDiBEVidCfNqtxwzdIcLh33AB78HOoiF10Cz2tsVP9PqTrMOwIF3hTePmc0q59Y4ObgkL2jaHwc5dgJc7vNlYsI7HHn66yC1o8WmSKN%2Bm6hEiMnJRswa%2BQc18C9cPK%2FxcR8w6fl712iAU%2BkUmjFjEhcZk0LDkNVo%2FEBujhpBVGH5vR0bMJf%2BbCx3yO7d%2FcEAY8R4G621O27G9iFGtXRR659Ko8CUmK27UJ4XaUfgeaEYl7%2BJt1P%2F3i3PsEf7UsTZo86L3JySNK5zLST6WJyfXlnV5LYckrhXq0oyqsAW4N59M8PLt%2Fh9iDKWi%2F1qADvl0%2BBRcmllZuAKGXt9zwP7r9Xnga2RlFKWl3lIq1ayflPkbkzB%2Fg%2BnzV5lcS%2BEGEsslpmn09vZR1GhPZ5N9wPRFmaWr8atIs%2Fl%2BVI2b%2Bdlh1TG9WXSTdJWOdlMzPY17yrKgfjYbd5dlzauTz2LxS8%2FoZBc%2FLR9EOJX0cf6KGa2L1U%2FcStXB6ocyBbFfDEhV8u%2FMMb1IUqJyLy7PiA4q9lYOXqYr51CCEvVDHRL1LZBA0spb2vI4v8dyOlUZAIG4a4onIJi57hzbLhA3AXNFPStE0KYMEEHcosATIkSBR2Eg8EQ9KziQWRgI4sw%2FUzhI9r86cxXi%2FqIT5wIN%2Bk89IzSQGIQNkqwvAQZ8gQFxGwQW8EA9IyxQNQZYIE6Z8okF%2BatxBRj0lnpGYDBhYRjIAgx4BIOqrDuBCL2lnpWrILGwD8ThDZ7iiMUbEgQQ9Jl6VkCgsbANxIEFnoCgcDeGwIE%2BU88KByYsDAKRccsTDlTfeCPgoM%2FUM4IDUsAADkQKIk9wkD00JkCgz9SzAgGNhU0g0g95AoHodKeAAR6oZwUDUxa2gMg55AkGigfUBRz0n3pGcKDILKwCkXPIExxkbqIQSNB%2F6lkhAYtziopIOOQJCS5ssIEiUsAL9ayQYMrAJlBEuiFPSEBTCQQQ8EI9IyBQFRYmgSyAoMOLe5KL6HwUYC%2B8e1SoLA%2FUs1JZncXaLVIBu1TZWFlv4H876Idf%2FfF%2BRJ8WwSIYxwX1XSmvkf9ypzZmsd6K9L0ulVecy%2BWFekYrrKawWGHjLqG5gTkVrdJavPPWcS2lpMjQMWeeF6rZysLrB8IKUnQeatxiHP26jSZTkuns7hXfwzvHhGZcr6Tu4%2FC%2Fer2lxUnPUjoDAZlnmAzAqNDjqjmB1I79N6ox%2FjPhDS38GRfGRc8o%2BLdU8rPQMDfDUZEHLRCgxzyvM5Mu1U9pTO01Dq%2BuTTMu1YmRkwrdMPJ9RNMWN9tLRqmnU%2FWNS2EijpY6CkUs5WEzqdPbSZ0qpK4fUidP5cJXsiYcSJ3RTuo0vqROH6zUSRNVz8tK8kG3g6Vu8saNNx1K3aSd1OlC6vohdeUk5tZYd0Spm7aTOkNIXT%2BkTst%2F332STGiPZS75ktuhMjcVMtcPmVOTb1PFomJEQ%2Bu30EnthE4qh6KE1H2I1Bn6NA916qT%2FUie3lDrO4ibDlTpZ0%2FPf2Euz0PosdkpLseMscDJcsZMkOf9NImOs9l%2Fs1HZiN%2BFL6iaDlTpFnsh5sJPaSp10PKlruTWRGLFC7D5c7PTEOudJ7FruTUiyELt%2BiJ06Huf9WC7EruXmBF%2BWnTZYoetuR4y6JgWhYyh1vyywglZZ1NSKSDARzcuwNpEU6KPfYBU%2BGueFKEm%2FSZNbilk6NjLpYfCCGM3onTH7%2FIlyxo3v0k%2FcVSR40G5OozwR%2BqU1SQpzOshfK7B%2B2IQyfbqOpJg%2B9zarUXj4kH63L%2FvHH5VZHw54RPTjc3d4%2F7FBwtaImgNTPiql7j3pG2rLzSW%2BUGO47mCHqKEaeVuLJWpMalGjwuUTqNE71Gi5OcgXaiRGn4CNV2BD0oy8rcwSNqa1sFGxBShgo2%2BwkTg%2Fw86fYmhsSPuSD0ENXe8ofeqYxoY2rkMNrSLQ9ylRg37O2u8fXLTcmecLLgZsZXSXbnlMK0OTavGiIuVD4EV%2F8EI%2BAC%2F6Cw4DBAJZKuTo65L250SfyJIx1lRZV1om7EyV%2FNEVQ5P%2FVKaaok4miiGPp4WUxw4xQq7FCFlgRJ8xomUCDF%2FJ9NPhIklnufRGIY%2BGpUWh1KJFRTqfQIv%2BoEXLvCW%2B0CLJZBZw8ZoHIk2PF7FQa%2FGiIoIu8KI%2FeNEy44wzvFDY4cVHRziNgkfRfjt1ohwvYqHV4kVFLEzgRX%2FwomWqIF%2BHLBkmCn4wXEi6moeL1qcsFX0qHw0u9Fq4qFiIBFz0By5apnhydj52PFy8GBc2RFofkFXH2vHckdocT03kePYaL1omd%2FJ2tFkeLmDoUj5Pq%2FXZZrV4Np8lYNSmd2oivbPXgPEpLn2Rh4wXXTkk0lufV%2BoQLmrTOrUKy%2FVTwsXOh%2F1DC71lOidnaMFwd2QwaPHWpxm7Awu9NptTF9mcWbAYUy6THnfIMulPH66xY4JQVIHr9g9NWmZ78hXcGLLt0Vlw44i2h16b7KlXOMGfGE76hhZyO7TgLLQxZLjoLLRxTLiozftMBFLAhWuCIDRAVhbpz0J%2B0D%2F0%2BBzXoLFEj321PX5IR0SP4oGQ6ZgD9KjNA9VFHmiECT54zDgv9zBYb%2FtofbTMC30dP0wEbOKi3W3px2YyKCJ9LIpIw71zp8vLFAuX0DJEkdrsUL1CvD4lithEtjd9xI2W%2BaF8RUyT77YN0mnpKmKqqcczO2rTQxN5%2FPSAsQ6%2FRdtDwGiZIMrXLeHJh5WHCBgTo3Dyte3X1VTjeIBRmyCqiwTR%2FD071FcJMJ0F00ZO%2BANbPcSRlpmjnOHIcA%2BmdIcjmqQWrv5ihyO1iaO6SBxNN1dM2tfLR0MG%2BelhStp%2Brj3gbq%2BwSWdk%2BX8%3D%3C%2Fdiagram%3E%3C%2Fmxfile%3E)

<img width="1004" height="161" alt="se_proj_3_1st_uml" src="https://github.com/user-attachments/assets/f30c3f4f-0dd2-4876-9a73-bc1602b84f79" />



[UML Request Flow](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&dark=auto#R%3Cmxfile%3E%3Cdiagram%20name%3D%22Seite-1%22%20id%3D%22gWttRObwHHih-HlRBOAE%22%3E7V1Zc6u4tv41qXI%2F5JQBM%2FjRcZzdvmdnOHZ2bvdTSgY55m4MboYM59dficFmkg1YcsBWV9dOIlhigdb61qAl6Uoarz9%2FuGCzuncMaF2JfePzSrq9EkVJ7UvoB275ilo0SYwa3lzTiJqEXcPc%2FC%2BMG%2Ftxa2Aa0Mvc6DuO5ZubbKPu2DbU%2FUwbcF3nI3vb0rGyT92AN1homOvAKrb%2Br2n4q%2FgtRHXX%2Fic031bJkwVlGF1ZAP33m%2BsEdvw827FhdGUNkm7id%2FRWwHA%2BUk3S5Eoau47jR7%2BtP8fQwp81%2BWIR3R3h6pZlF9p%2BFQKpSBD34flfyYdY%2BWsL%2FSZcSTdL07LGjuW44RUJCoYMVdTu%2Ba7zG6auDBVVAgq68g5d30QfdWSZbza65jsb1Arivyy4RI%2B9cdBdSyv8EPgJ%2BEGO7d%2BBtWlh4XmBrgFsgJo%2FVqYP5xugY9Y%2BkNyhtojfd2AFMb9XomLhXjeZ11D%2BCfBXvVkD981Ezx71N5%2FbP69DtkaDsMmHn%2F51zOFIRx8GuuFjkh7Qb2%2Fxz%2FA5i6Rh7ELgwxfTM33Hndi%2B%2B5XcgkZgkSdDbZt828rFPCeqkDxQID%2B7zjtGH7vsJeNhKD7mGnUPDdP%2FEUDPfwBrJMd9dD%2F6d4Jan1Ef24%2Fg5mmm9jv%2BDs2oJmtgWjXInlahklW%2Bf2ybeo3bwzG9RYNbkcbbhBTPZurd5xsTAZW7l8Bxn782FUm2fM0g8By7ImcL354HizWm%2BgcP6pbsHr2dawLrJvB93FsZ8dJ0EYnj7ji8Qy0L4MG73ZXuSPeViP7vF7W290fx9aN7HfvFhB8RhdF7R79vv8RL%2BMcYcwzeoTG1PR%2FYOpz7aam5QUbBgn9s%2F353EBKXfmrTxqMLP7ziU6qQOxtoY3l9MvXf0O1VIfGghcwoNF4wkva8RPSyorjraO67pv1W3hUCY9NAT5%2Fam8BHL%2BAVgCChHoevWgSKkutZSEjfED8gxICHYL1Is128LaP6hes5ZSqhz2po%2FnoGJApXs4AQXt190QXybiAgqJ7lOL%2BDzR3QA8v%2FuvkKP0YP4n%2BfXXO9hkbpQx07prgL3ZGtGJpjx%2FaCdQgtYI2taPjINOl2fKPrsRJEnT44frbDWWDbYFFNsvWivh0rIZ0WgOjqMhqnX2b5QMaXyTqQVpIJdlkKElEJA9KmoRJq6Ehk3TvHXVe6exGYlrE1jY8b30Ry2CuAinxzJd8SejBtI8YiEkbFH8SHa6%2B04yqM%2Bg5AH2ANPQ9HCVU%2BZNbghS4pcn3hZ8qrjn3wH9BZw9A57K9SAYQsx%2FHRxy7aEEUljhTifsTBMG5IoqoklABxuPO27X3n96NfYte%2FPAwY8DCAaRgwCywUyZ5BLDCf3j%2F9nLxO7kfTn1steAI%2B%2BjZ2196FMExk58%2F00sEJxjzgv2BPp7fHQ6lg3qPekbvoBx7qFXmMy2XsYm7d0QNOV9RFiK5PwPOmBpHmaJja5mdSMCX3syglDOUsSm1vOAalZI5SNFFqBfXfj4GPPEEU1iFRRqb4zgVva%2FxxOw5VkTrMJv%2F5NZk%2Fv%2F578vfr%2BM%2FJ%2BN%2BPv55fx48Pd9PZfUWNGs1%2BvN4%2Bjn%2FdTx6eX6e3NahepvPp8%2BPs9WF0P6lIdvPr4RaB6%2BHndWcIbPiRBMI9w9EDLFzTcg%2BXHAPsoKyKzBZD9gTo4QcC6qWF8xzbHn%2BCLyfwp9tmzImO1BmYaccS0%2F5wnWBTO8CPsgIkzhCte9jNZp96ON4oKIOCUdh6psmMgNBnYBQUbhQoGoUZ%2FD%2BoJ%2FHfZViG2eR%2FJuPT2IXKAE8gjFidPj6g30bzx4fOm4eD4kZ2xitZlh3aVRZsbj6%2Bw3xoxdRHwXxoLGIKlZsPquYjnlPqtpHA2QE3epUSZ7V0wmNPAnvP%2FaWZ6D333zsL0yJluIl0hIw28f7SDDbx7tKMdvmMFjmJv%2Bf%2B0mx72f3xiM3DPEolimgmwhj590jnzB2N5ZAoNmFypSI72OIgC1Pj80c0tTjScUiEQbs%2BDQqk9hJ1QVUTP2I7YVFu%2FZIbyFq9PwStMX90QEsrzzM1nEeqOJlKnMPar0Xjg1ozJmvJ%2BKBWEMfvDSa%2B257UZiGjmiYijXw17wk9%2F2UnHHU4SJNVSXFU4wKLYAMuQjKyFNflIi3oDbjJkB%2FWnXrcRfJVm6uYjKyh9bjASlybh5CIhAH1no9hovbzQ6LD5QoHnp8q%2F6rDQZqMjGV1ucBw14CLkIyMmJW5mKVBtQF%2BxYT7sLkyL%2BMsfKe4KXcYtrzkCffZgcrcHJitKmEjpii1MTUGJGOG6g1JlvSgRavDUZNxKVDu9SKry0nesawhKQXS%2FV5qXZ5SjmttptK0B%2FxgRskOoZ%2BUXacLPQa5dMf1MFfoMRgIx6c7NJ7uoJ%2FuuDW9jQW%2BooIAf1eu2%2BX0x%2BT%2B9XY0%2F5MAi115C8IAkeNEA3ir6XKy3vhfvUjCSHC%2B304so%2BKQxIcLPSo3V3AdM1evywTsH%2B26%2FTEp%2FBgIYga1FIFFknbIUYsiaj1B20ByEYuLNzLA5kxAK9aInRX%2FaYbJ6FSF8y5DnSlrLsuS7WaV9nVYXiZd1h8IvUTcA0xPDo1yzd355BEulYtTr9LbEnEYh2C4frdnw49Z41HFCSfUwa2j1x%2FDqrNqu9nBPx3LQC8eoxRh7g9PvGUiSdNOIXbUBflhN6ZtpB61KhCG2TcHWR0zFYlkHnEoXkZffewEdnpa0cxM%2B9MvFJGHSsaIDPI1zlSMSGK9mlqRpaZDXS%2BzIgtNHiCzdxFWxEw1xBoE0K34CqZdYm6TK258JSU8Zr6%2FtAhWslOvRLzspNkif4ducB%2Fh0mizcZFck1zSCJcqVGnsh9rI96X6kOODebWIZwNtkA3lBVnJAtpQogFoJau9uVtM2S1%2BzZrk7sIMViH%2FfVaYBcMrhfdVGfnvLyWzVpWpMrNMlamyq7krkuXyr4eppiWJ%2FcpUTT5HZjrnEM3CT2B1S3JoifaWMELKA3TdkdvY3cUr%2Bl5KFkNTgnJJ0Ipp2QKUS7kEx0ChAeUih3KaUO7CWHWMM8tyRPpw%2F3g7eX2aTV5HT0%2Bzx5fJbc7XISFDSFeZJqzByiyiPSqdAixrSq836skZy7r9znxPlnTtGKSat%2B5IKVkLe3hzC2SPGspDVMYU9nDMmI2rj9G4fEwqpa%2BOeMkod9XwBatFVQjm8YAgFkJ72CQso5oB23VDfmA%2BCxbfXtJD69NhkioWfA5BGGjZfNg2XIx9DlWm4XMcufcXz4fhn6fMhxHxlOfEWsV9DIu4uGRKnKb9pnSVrBxOV%2B1imm3%2BnQLc8D1GThPivBZNaHeBgGetOpa18t%2Bj6tQbYLzVynXFcLkl6XrOKuWgfkPeSsq5kNdiP5e3EmlgOt%2BRhf7i%2B7PLWR2dR%2BrCS6bXs9PNd7BLMvAA%2FwT1LmIRnOUk6I%2B7keUBi3oXvjPKCcD5LL3t8gUt3Oc%2Bic%2BdCFrdyeIZND0vODRZ3B1BPKEDncBt2oEW8xO%2Fap%2BBA823H6GJ0Wc76dvtfGg17illM0VJySmuqOYUl0o688iVdHz2BP884ewJnzrpAvdxwBlv89mBuZOhmEMbSc3HchTAhi%2BAY7BsF%2Bs81n7kpHf%2BnKZIcyZ%2FPc9Gr8%2FT559VtxyOKOa%2FbhoQPY%2Bef81rkcweKzwj3GY%2B7LteiVuabvJwO334UYekyWOi7Tgr0rjwDcmbCzKZqsTszDIXS4vJEld2i4T6l25BF0dC%2F8r5uVlKHwkVrLMrmutY1TZc89%2FDhdIVgtTwuCp%2FDoGr79as5c%2Bp6o6eJSnRHpWTnjzoB5v0eFY6SQX4PtBXt8AH%2BPitRJAqka6Br6%2BgF58M9JySj563f2uZvScUbQJvFQrEs5PkvatmmG8htkxfle%2Fv7HqjkuQ6gzIRwlNoe1S54Yeuh6Rwmz08tN%2B722S7nNSDtq90aj9Q1kpS%2Brl00TCXLNJoZPSTjBN3A6m4gXEGOdYAvEWH%2BxKdW%2BfwOLAFVjY5RHC3FR5S61Io2e5VR7oBSbSFT%2BXdXowHGsHUOL50wqBRzc3%2FiZKUjxlpoMWRy0N5hgr%2FPGGGah8evRYFlkPU90MU9hE95ET3TC8coKKjjNEHuq7j3p%2FyKMFKqaohg0yVKHIXhb6LMtcBPtfyXJJV8THUN2mDPF0j5SAnD8z3%2F7hPFhqlXC3QAbLdxsfpxVsHD8iOwRZmqFDMOXbWG%2BDvOyAb1z7dmXaaxScX4lYykzpASA8mn1APQs8z4TJumEP33dQh4bt4oWyEjk6VGD0O%2F56guzY9D098gMDWV7mlb1jIImDbXa66BrILAsgki4PPYhmHQ1nttG00bjfA1ZGIeT0Ti%2FGT63x%2BZSU7bKrSW%2BIrp8S9p5cuWKSQjzm%2B4EIu2QQqf4TkYCBmzRONhfbikYveuH3K2Kc4hXieOwp23UE9bdlFfhGZIjLwLvkaMgbay6umOsw9LfXNB4f5oikq6suXCzFQ3zNdNcTV9wj11RgoL19OwkR5Uye63Ifz8lxzO8E9Lc1Vcnu9FKaCKKguX2XAQHWz0zV85rhT3DOyuyoLw8vP7KGpvWGFWpQN9biudoF7WrqaTzALLEwtr9Snqay%2FPO4Od4N7Vko6YGBRE73nSkpFSW8B4vdHdMoZt6pd4Z6VwmoMrKrET7qgGsC64G0NM5vccEVtLfeMFBVpKgNFFbmi0t3PIio5O5fKP66xx2iszMK08oIoqr6wCSznjRvYLnHPSl01FgaWV0BR9YRNF%2BLl0%2Fin5zsuvOIa2wHuGWmsJLIwsLzoiYXG8sxwV7hnpawyC%2FPKi5xYKOsoQKPIlbUD3LNS1vwJVlSUlZc1US2MKG7HxBW1tdwzUtQBi9JhiVcw0VTU%2Bca0%2BValHeGelZ4qLAwqL16iqafEDfi5uraWe1bqOmRgVpNDY7i6UtprZbdBB1fSNnPPSEnl4jZsFJSUly7RVNJoPxaupl3gnpWaKixsqcjVlKKa%2FgRfTuBP7aUF%2BFL0jnDPSF2VPguryquWaLu%2B8QGeXFPbzz0rTZVYGFZesERTUwv7XXJNbTP3rDRVZWFTeaESTU3NH4bDFbXN3LNSVG3IQFF5kRLVWHXPGVxcadvMPS2lFcSM0qp9BluXDnixEk2lTR%2BXxpW1C9wzsrCq2GegrLxgickyVl4J0R3uWemrJDPQV164RFNfS04s4braZu5Z6arMwBFO%2BuC6Sqdqv%2FyUIK6vbeaelb4qDDbtl3kBE1VfmHS8Flfb1nNPS23F7JbfqsbAJZZFrra0513jA9q4qrafe0YWVuuzUFVezES1RNgcO7YXrLlV7Qb3rFSVRWJY5tVMVBNNwEf38oRwJ7hnpacsEsJyXMsEjTeY0dIyxXUCV4%2Fvkgq6DG1j5Lqhpi0sR%2F%2BNPgVquguV7rYf%2FTWPBlMQ8ejudN91AtuARnxfQeP74X9k1cXNSc%2FCdgR8NM4wfoFkW%2BSMKpeNCXpl9%2Buv6I7%2Bv5Jvgxv%2Fjhvjpk%2FT%2F6vQ8neOMDPCUZMLLYDi2uy3Tg26QB7SmNsnx8T7JSZSIQ5yhTOqqmT7iIYtJttJRrGnQzsaRF%2B00FEoYttvWE3qlGZSJ3dL6qSzlTpJyPkMSrKhcW2pO7Tgl6LUqc2kTuFS1xKpU4Wc1A1yfbRR6rRmUicMuyV2IjuxE75X7GR1kLWww0FDqTu0FwlFqRs2kzpR5FLXDqkTBCUHdqraerFLWD7zcGJwtlI3UGVK4YR0aJMImlL3aoEFtIqilshjWrCQaP4M70aSAj3zv2ARXupnhSjJI22zNPl009o08AYLOTEa4X2M9qWOvA2wr8rSFLib6yjbMUI3CEKYmUC%2FLYD%2B%2By2U6Ws9kmJ83X1b9MLoHX2dfvqXP0pzFx%2Bu6UMvlJjwFNZU5iLiqGbyolTwjklEKIngnzVyMASOs8lDCMViKnbIIRCRo5itvkzkCDzotQ8txEtAi0QPOFzs825FRTkZXIhEuCiJmy4SLhAn%2BHj30Nfww2Mu2wce0iWAR3KcBg9S9gUph7bIoogdEhE7SgTrIrHDhcDwwueC5TL8dOi9sof5tQRBBheBIApHkIMIMji0axdFBBkQEWTAESSDIHrghl9H7AfZ449agh8XUXUhM5yJPBv8GJ5uJlKRifhRUlhxkfjRzmRHw2qZjqEFn1SpkBvtn3BWRSHCRUlFzEXChb6C%2Bm%2Fsb8A1MK32AUfDgqcSb7LFwJEsEjvDLKkg5tapKn25IXAcOrGVIm6oRNxQOW7kcWPjmzb0Wuh0XEjZ2vl6HdTK1k5ZyaERsUPj2BGFKJtkegV%2FaLH%2FTwBds41RS8MKxI4ByJDjx8EUqSJoOfxgByBDIoCUyNVlAkgbcxyJn3vmaJFsoMrhggwXstzP9sQQLlRi5WgikBwuWgkXDatFuwYXKju4%2BObURj4j2hQtrgU52cGHeXSiEqtFE3nkaNFKtBAvAy3Ot%2BKLnnOhiSerFlWJ1aKJQHK4aCVcNKwP7RhciOe7UJyaczFQ85kLZmBBLA9VeXlom8GiYSlo18DifFeu0YtExHzeghlYECtBE3HkYNFKsGhY99k1sGAYiOxu28GFcDqwkJIMbqzjQl%2FTmsGFWAhEmKEFse4zkUeOFq1Ei4Z1nx1DC561OFyBIQv5Cgx2aEEs%2B1R52WcECc4G2i2Ei4bVnh2Di%2FNdlEav4EI43Q4aKrHaU%2BXVnq2Giwsp8OTuRZtWwavEAk%2BVF3h2ag2r2rC%2Bs1vwcb4FGAMp5yQ03sL5WpSVk%2BEHsb5TLRGsi8QPw%2FQ2Fvhqn8eRGJnzRgyGq9G%2BexnrUMimLBsjhiLJJ6vC0Iglnok8XjxiLB13DfwWAkbTKk%2BxU4hxxj7GMLdyTNEaLnw%2FpY%2BhEcs8E4G8eMRor48hXgRknK%2BTofRzJVeNIeOkTgax1FMrEayLhIzWOhlNqz27dRjV%2BToZsjzIORlC0wquUzoZxHpPjdd7tt3JaFrz2S3IOGMnQ8iVcTWGjJM6GcSqz0QgLx4yWutkNCz8LJkTazFgnK%2BPoQi5KvHGG3Gd1McgFn5qJZbosgGjvzShZbQQORoWgYrdSmic8T5c9A7yO%2BFGXBqxClTjVaDFk042oIVb%2BGkNC0K7Bh0MT0k6G%2BgQ8jMxDKGDWBGq8YrQ2OuAvr5KHZHUQuxoWB3aMewQOXa064Q1jVgfqvH60ILf0TbQaFgS2jXQYLgE5bvzHLQwQ06OJGLvbhArQhNxvHjEWINN%2BjjGvuHorcOO5DjQ2thRUsTXYuxgGax892EFWn4VidpwafwJg5UhsTp0WCJYF4ke28MK2hqrDBuWiXZrNvaMM6TSkNZSlBNmSIfEKtFEHi8eOXZHFXg%2B8IMWIofYDDlKUuAXihzf7HNIam5jDGWQ66ONyEEsFk3kkSNHy5AD%2Fek6mMPdkLtgs7p3DDwwk%2F8H%3C%2Fdiagram%3E%3C%2Fmxfile%3E)

<img width="991" height="131" alt="se_proj_3_2nd_uml" src="https://github.com/user-attachments/assets/2376c4fc-cf4e-4819-a291-adeb77203cf2" />


[UML Authorization Flow + Profile + Home](https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&dark=auto#R%3Cmxfile%3E%3Cdiagram%20name%3D%22Seite-1%22%20id%3D%22gWttRObwHHih-HlRBOAE%22%3E7V1bc6M4Fv41qco8ZMtcDPjRcZzubHV3skk2O%2FOUko1is42BAZzL%2FvqVANkII1%2BIcCSsrantWOiGdM53DkefpDNjtHj%2FFoNo%2FjN0oX%2Bm99z3M%2BPqTNd1x7bQPzjlI09xDD1PmMWemydp64QH73%2BwSOwVqUvPhQmVMQ1DP%2FUiOnEaBgGcplQaiOPwjc72Evp0qxGYwY2EhynwN1P%2F47npvHgL3V6nf4febE5a1qxB%2FmQCpr9ncbgMivaCMID5kwUg1RTvmMyBG76VkozxmTGKwzDN%2F1q8j6CPh5WMWF7umvF01eUYBuk%2BBYzNAkUdSfpBBmKeLnz0l3ZmXL54vj8K%2FTDOnhhQc%2FvQRulJGoe%2FYenJwLINYKEnrzBOPTSoQ9%2BbBehZGkYoFRS%2FfPiCmr0MUa4XPxsI3AJuKAzSa7DwfCw8TzB2QQBQ8tvcS%2BFDBKa4a29I7lBa3t9X4C%2BL%2Fp7plo9rjajXsP5e4lG9XIB45qG2h73offXzIuvW0MySUvieXhQ9HE7RwMA4a4bUgP6aFf9m7UxIwr8TlLNIRGM%2BqWZEaVE1bR7jXhLhJ01o7NYOeat8eOteqxj4zWYusKosff8XWCCh7aGs6P8f0tgLZqv3jcuZ49DfL6MLUnj7cunFSAf2yQ8XwPP3yhnNkYb9Wi4mePj3yD8NvOnW7DJM1JmO%2FuthkTv%2FY%2FM1S0%2BZszlizR5%2B4IYT8rPmaf3cjHbMxWjb2DNfYgbT6%2BIV8JvumN%2B8TFIqw3j%2FdVWvIYJDduP3aIgOaTjLXzOuezd4tVaUQ9otF2Pr2t69GOMpPqT9vECdZOzd5t1aeA5puVyMLX9792K0EtFDOlEqtU3Ga7pAo01mzJDRhO8le1xY728wXMA0%2FkBZ5iXXw3QKz%2BNt7acM7MLbKqq50CyzcDoKf0zvF79B4SjNVrWvPQb0R%2BE01DsQpnIgODoQP0JU1XCaeq9e%2BtEFTyJDg5sgWqYryR%2B7XvqIKmCYcpAkb2HsNinzGM5mJdS9WSAP%2FcmDb%2FWlfDzWl8s0DYNVEfKzLv9iuCyB6bUXwwlIYJ5Y6%2B5MNjLjf5M0jGF9CS%2B5K97kyUu8SelVJui7B4JAptnPcTEMRjFEtug8Aa8QTWqSgmAKH1KUVBr0wPXhLnS%2ByFF2Gd1Rs32%2BT7lsqjNfaIvfUlR7oNHI3GWYTue4emz5h4F7D1000dP0fInSbprU6CW4utEyxqjqf1z64fQ3dM%2BTAETJPFzrxVU4XS5QlofiwR8sian4v%2BhRPH6PUC%2FdSx99rfpekt68%2FILQRa3U9jofpAM6wNXAGX1zw8BZA9rAaXpPo%2Bzbyt59xr71lX3jaN9%2Bgu6YtzpXUKbe84DnTyu21rc2FNtwHFqxB7ZDK7bNQbEtpdgcFXvoLrzgO5qFrmg3NpOj2x%2B398%2FD0ePN03gl%2Fl7A8ELz3De%2F9s0fgFc8YDfTkv%2B5w2VFRR6mIDiwCHaYYHJooThE8nho7%2FAL5X56UQT%2F2Pk%2Bh5XIX%2BfAMvnbbC0kg1TydqpJffcwWS7286UBVnDU0iOYnKdg%2FY2D5PyIzl%2FVRlwMeib%2F4IatbARHG%2FFtCWJX2QhlI%2BS1EXSh9PUOBi76OB6Fy2D%2FQvfwv3CaQvewUsMoihEQHFjqLobNCq7CArsKyqCoApjNGL7EMJlnY5kIamhNS99taPsDmzK0Bg9D6yhDy9HQXoPp0k8%2FlKlt39RehW%2BBHwK3S%2BaWvJOIJvclF%2B3rGMxw2PkWKawPPlaFUfoC%2FgAf4TKVSboFsE9f8Fln72Ft9AG9Zm0MOFibgbI2HK1NocRdsTR12iRT76UJ6%2BuG0ecfsiHkYaXcPJWb2Ful3OIoN3aSzr3gxQdpiWeWOz83q%2BSMcokkD3hBKRcu%2By0Ol1GxrL8%2FTNS7ZqRn%2BGneO%2Ff8NctJFTuwLb6QZG9GkR1CwyfV0J%2B2%2FR4PQKrh5CtA%2Buy37cNysvBSTJNKw%2Fge%2Fr2EifTglPHlXC%2F9hl%2BGIixvp7%2FhMjfBKx6JA0tlvNkD8mOK6wHZs%2Bm5Kmv49jJJlJV49Mpc7cgLgvWuitoCYfz4Ee1ZZNWvewiS0of09p5N0iCXuJWoFcV%2BYpT1gL%2BNOPiyZv3t4gPKIKY51m%2FRQ%2FZ2hDYNRIVAF3hZFCHZbGWf4mEEAyy4d970N8UC38ZN9LOo%2FhMGyfOEyCAtk7vp5Bc5znqYwJ9xYNELJBuqXebkeTWqX35elL%2FbsS%2BjyEap%2BMbzitLUlKc1sfqcAoONp7TiV8iS2wmNhUdEiaEauF0iW4a0vYQ8I45eh%2FFir9yTpee7K0i%2FjVIvDJLNLRX9y7P%2BFaMGL3AL1WGpVDGXKVwktRXv09E0BGgAFjBJ8JbQfQby89EvEsgquaOa5ZiUP2qZFVKDzsMh1ZVDyt8hJaushTYlQxdEaTd2g9K69cPLPG2wwNOQdWHtfueJ0r0k5dEwJvIcexTI0btpMhhMhyiBaVbheQDfmta810rAOmTwPfRdvE0u135GQAA7TZRBopYB1tWwG7xEyF1qbl5kr6kh23wRIiPhlQwkc9mhZrseHrhsXb1kWryAsQDUAKpXy9MlqO5XdtfpFol4FlBtazygWm3PbwGqC9KOQmrJkbp%2BHhVQK6DeAtSGYRkUUDsOD6BW26BbAGpClFRILTlSMyZSQbWC6m0%2BtWNUyD9cfGq1o7cVqM4UCYfrl8lPkE7n3cDph8fh478fnu%2Fux8%2FDu7v726fx1UoftoXwSbnxr6ubX98OKdKkmfvxP8ejR2YZWQZ7iyyx15QWWYbkPEVNw6LQxjhkJ12Vq63kYC4ztAN0Js2EMswKp960eACd2uHcAtDRa0xP%2BUpd2Amsu%2FsxHI2%2F3%2F64Gt8%2FP9083Dze3j8%2F%2FnU33guJNgo%2FP978ZBWVZUS2zzkbk%2BYgwZnx8SrXHvTdRKq1yJq1xj2WYfM395ISI%2BOhWBI%2FZy9xHloxXkikq222aLyqFE%2BmVLNzvCX2z9s%2Bc5MFrFk9eo1Ts80BHZDhYvzU1u0WjB%2FZGdQVrn92NphXonMdGIipbKEh4amistVo0WGr6r7QGWozBtSXOu4GDCCyOOWH8oxrG2e%2FgTQF0zlCN4CZc2SE9uKhuMVE%2FOv%2BHJ%2FyxjjbjG0E9g0IXUGMFh%2BMPrUCp2aFMaIb5JB18inBJWai9uceAU2fGYAhK7rWKUr3et9AkWt3R1m0Il9oZo%2BOfnLZHaW2PvJU5PEiSj9yG5EobZWh95y0VSe6udLWXp%2B%2FthK2p9JWLtrakW1CSlE%2Fo6h2C1a17todpaefO0v0JwyW38Mk7UqoQWntIVpb2Zd7YbRhXXWltRy1dlTegjUOsslQKit%2B73kbWp4aqujvPDW00E18mmYAY2VYZeo9N8NaYZ60YVcVF7qFr1a84KFUVqbeczOsdGC4lQ9YxYnlur6jTsuSqPctRZraiQgrTifXSFMUjcJFBJRllar3bWlsGyuu6g4RvqZ1y8lZSmOF7X1bGmsZLWisIju1obHli3KVsorc%2B7aU1TFbUFZFaOJKaKqcgqkUVeTet6Soek%2Fjr6jEUCtF5aKolTNulZ6K3Pu29FSzWtBTxWXiqaf150MrdRW5922pq9FCeIns9lHqyokiQV1sqZRU2N63paRmGzZV8Zh4Kmm%2BZ1SpqQy9b0tN%2B4MW1FQRl3iqafUmKqWu4ve%2BLXW19RbUVZGWeLu%2BxWmVSlPF731bmro6DpmnpirWEk9N3bjzW2mqyL1vSVONXhs2VbGVeGpq9S57pagi974tRdXsFhRVkZQ4M%2FYXMP9gVboqQ%2B%2Fb0lWjDeqD4ihxjSttPxxRKa2wveeltOQGS6K0bazZEK6iUlpOazaK%2FyBL79uyrVYL%2FF9T0ZV4qulVOF3inasPAYiSeaicYSl639qqTWV3jcbjZHazMN7QnUFKX%2BtUOFzG0yKXuaHVMHCHcZzp3MQPp7%2FRWKCk60z9rnr5r4d8NpHHgKZ3jQJxuAxc6Bb5NnS%2Fl%2F2PrcQ4mdSsraYgv4GmGDurRqnrJgXik5L%2BzHP0%2FlEsaWWJfxWJRdK7l%2F65kfJXpSA1xXlSDH3kIr%2FSY10%2BkJ89pUVv78Ls5q%2F1Po6BRomFZdt0Hfm0FcXWkrFZU38HIy4f0Y2KMhFbjeF%2BUmc0k7q%2BkjoxpA5NhUPLiu2IL3VmM6mzlNT9SQ3OV0mdSWZQJqgjA3ug0NlK6MQQOkej7attVKoQUeisZkLnKKETQ%2Bg0zabtq%2BVY4kud3UzqBkrqBPHqLOJhy%2BTVOc2kTlMWVhSxGwx0GuzIUbMii92godj15BK7fmfFTtP69D2%2Ffafp54S243A5fmJHaj5Y7DbD8Ersvkbs%2Bjr9FWv3mxrZI4od8Ye7HSZ22pO6L%2F6gQOBm0Da2aexk59nyPKXu2QcT6G%2BKWr8GzpBo%2FshyI0mBifc%2FMMke9WghIiuFq3W46oLiwnPxfsaKGA3xsQHr5Z3NxcEkwlf11qw%2F4Wou8mUsfAelpmVLTuivCZj%2BnmUyfTHNpRg%2Fj2eTc73fzy%2FBLP%2FxR%2B2iVII6neAhK6%2Fm5305cD2qVuQ%2Bs7TU108CM%2BzuWip%2BS0vO8SyVzsQMXWFGBgkvMJ3OIUaNKA5fPGrztiDQ0XB9UC7oIGwZ5W5sgQ7LPh50GEzoMBR0ZJAQQ%2BAmWbsgXSbi4UbDFV65cKPFb%2BPOwIZuEnO%2Fgo32cMNk4kaNYJ0obrheDKepgJDRcH1eLsjo7uIBv48UfcPRaA8x%2BkzEqKG4KcQQCzEakivkQgwV1tjj20SrLCC1iBgWEzFq6IkKMcRCjIbEGLkQo8XFk84gxsCh1%2F7aRAybiRg1zBeFGGIhRkNSk1wbJMzOIga%2F%2FRHrU4COABkOEzJqmMEnCRl%2BmAVARYOLhmQ0uXa2DNqDi65sbNEdbXA0tBgw0aKG0X2SaBFGMBAPLayGHEK50IK8QQe9C%2FLJ%2F1m0uNB3nAHBDyysHgssiDQqsMjBogfcBWZz9dIw9AUEj4ZMUMnAw1C%2Bxi70MK1qLKM18GDyQIk0KvAQ09PQTwIsWoxjdAUsnJ5%2BLLBgEkCJNCqwEBMsGpI%2B5QILhRVChTwtJufTUpxPeosJfRqzIJjRkPAp2d7vFskYXwwa%2FE5XOd4WE4vJ9yTiePKYUeWJ96aoQrzO2sMDr%2FfWW1DIA9GA5SSOberuigo3XDnmiorFpIVaNQv7Jwksgn66NKSEyoUWbQZFv3hFxdTpfSeN4eKIQVEmI9RSjFChwaIhG1QysOhuoIObb3HEoCiTDGopMqjQYNGQCCoXWCisECsoyuSBWooHKkFQtCEbVK7Tf7sbu%2BB3%2BO9RgxdMOiiRx5MHDTFdDLshHVQuuNC6u4bCDy8M3Tja%2FjSbyQi1FSNUaLxoyACVCy86vDnNHtArpY3h4njxC5vJACXSqMBCTLDQTwEsyEe%2Bci62fYwYFr1vpU3ngkkCJQKp8EJMvGhIApULL7ob7%2BQIF0cMeNpMFqitWKDiBzzthixQuS6eIjfHdPCLhOfFU9bxUIPJAyUCefKoMQ8TAcmddkNyp2x3N3U45DmwK2re%2BO6m4%2FHGbSa9kwjkyeOF%2BFcT2A2pnpJhR3epnvyufbvQ9KOxPW0m29NWbM8cE1wviXzwIaC70ZDwKdudfd11Nwy9R0c1mt%2FZd0R3g8n5tBXnM8eEt9hLM28jhn8vYSJgZKPp5cZyQYfW3Zvc%2BUGHqZHTbo4Q2mAyQG3FAM1BAfXEc0EGH6KBRtOrqSUDjRYZoF%2F9jcLtjuCLwfH8DSYB1K4JtJ8kZiwTAeHCOZErxbu7fMIRLqyjXe7sMPmfTk2o7CThYhpDIR0MpyEDVJNrV1qH70DSBwOd0nOLRHwFDmg4TBKoU2OKThIx1usnIIri8BWpxyq4ISCO6CeBI5reHo58dXTDtCtnkTfFEbNHhqn96IbDZIcSiTx5IBHzS6UhOVSrmVWB8aLDXyqGrVfwQjOb4cUxv1SY7FBHsUMFX3sl9%2B0dDBk1M6sg40uO5uoNKnfCywAZTGooEUgFGcJCRlN2aM3MKsj4EsgwLVrRpYAMJjuUCKSCDGEhoykpVK77WrsMGY5Os0KlgAwmKdRRpFDRWRpOU1aoXPeddJilYRjkotPPBj%2BPyNJwmKxQR7FCxYl9op9xiHu1nuYYRPOfoYsnY%2Fx%2F%3C%2Fdiagram%3E%3C%2Fmxfile%3E)

<img width="1024" height="191" alt="se_proj_3_3rd_uml" src="https://github.com/user-attachments/assets/653636a9-f164-47a0-8c11-c8212a4ac6f2" />

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
### Completed:

| ID | Theme | User Story | Risk | Points | Release | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **US01** | Auth | As a security administrator, I want to create and manage accounts for security guards, so only authorized personnel can access the gate dashboard. | Low | 2 | Half-way | **Completed ✅** |
| **US02** | Auth | As a security guard, I want to log securely into the mobile/tablet application, so my specific actions and logs are tied to my identity. | Low | 3 | Half-way | **Completed ✅** |
| **US03** | Pre-Plan | As a faculty/staff member, I want to submit a visitor request with dates, times, license plates, and a CNIC upload, so my guests are pre-approved for entry. | Low | 3 | Half-way | **Completed ✅** |
| **US04** | Dashboard | As a security guard, I want to view a daily, filterable dashboard of all expected visitors, so I know exactly who is authorized to arrive today. | Medium | 5 | Half-way | **Completed ✅** |
| **US05** | Verification | As a security guard, I want to search for a visitor by name, CNIC, or host name, so that I can manually verify them if they lose their digital pass. | Low | 2 | Half-way | **Completed ✅** |
| **US06** | Logging | As a security guard, I want to log the exact entry time of a verified visitor, so that the system updates the campus occupancy record. | Medium | 3 | Half-way | **Completed ✅** |
| **US07** | Logging | As a security guard, I want to log the exit time of a visitor as they leave, so that the trail reflects that they are no longer on campus. | Low | 2 | Half-way | **Completed ✅** |
| **US12** | Exception | As a security administrator, I want to suspend or resume visitor entries for any duration, so that campus access can be restricted on certain events. | Low | 2 | Half-way | **Completed ✅** |
| **US13** | Verification | As a security guard, I want to use the device camera to scan a visitor's QR code digital pass, so that I can instantly verify their pre-approved status. | Medium | 3 | Half-way | **Completed ✅** |

### Remaining:
### Product Backlog – Remaining Features (Final Release)

| ID | Theme | User Story | Risk | Points | Release | Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **US08** | Ad-Hoc | As a security guard, I want to trigger an ad-hoc approval notification to a specific host (Student/Faculty), so that I can process unexpected arrivals. | High | 5 | Final | **In Progress ⏳** |
| **US09** | Ad-Hoc | As a host (Faculty/Student), I want to receive real-time notifications to approve or deny an unexpected guest, so that I maintain control over who visits me. | High | 8 | Final |  **In Progress ⏳** |
| **US10** | Exception | As a security guard, I want to be able to log an "Emergency" entry (ambulances), so that critical personnel can enter immediately while still leaving an audit trail. | Medium | 3 | Final |  **In Progress ⏳**|
| **US11** | Audit | As a security administrator, I want to generate filterable reports of all entries, exits, and ad-hoc overrides, so that I can audit campus security procedures. | Medium | 5 | Final | **In Progress ⏳** |
| **US14** | Security | As a security administrator, I want to add or remove specific individuals from a blacklist using their CNIC, so that offenders are blocked from campus entry. | Medium | 3 | Final | **In Progress ⏳** |
| **US15** | Config | As a security administrator, I want to configure the standard daily gate timings (open/close hours), so that the system automatically enforces regular campus hours. | Low | 3 | Final |  **In Progress ⏳** |
| **US16** | Config | As a security administrator, I want to toggle specific feature permissions for different user roles, so that I can dynamically control system access. | High | 5 | Final |  **In Progress ⏳**|

### Product Backlog – Project Part 3
| ID | Theme | User Story | Risk | Points | Release |
| :--- | :--- | :--- | :--- | :--- | :--- |

---

### Project Backlog
[Link for Github KanBan Board](https://github.com/orgs/CS360S26nebula/projects/3/views/1)

---

## Wireframes

### Wireframes – Project Part 1

### Links

### Figma StoryBoard
[Link to storyboard](https://www.figma.com/design/9Me5uaUCRX8WqCouXx7nPc/CS-360-Project-Part-2-Storyboard?node-id=137-1691&t=9reMOKxj25ZCYS2n-1)

### Screenshots

### StoryBoard Screenshots

#### Without Arrows
<img width="1103" height="877" alt="storyboard" src="https://github.com/user-attachments/assets/9577adf1-761a-4b68-8684-d9d22513988d" />

#### With Arrows
<img width="1117" height="905" alt="storyboardarrows" src="https://github.com/user-attachments/assets/7563ca0d-195e-463b-bbeb-40d93d802d35" />

### Wireframes – Project Part 2
_Add screenshots or links to wireframe images._

### Wireframes – Project Part 3
_Add screenshots or links to wireframe images._
