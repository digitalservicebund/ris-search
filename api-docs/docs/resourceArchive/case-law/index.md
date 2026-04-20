---
title: Case Law
hero:
  title: Case Law
  text: This group of endpoints provides judgments and decisions of the Federal Constitutional Court, the supreme courts of the Federal Republic of Germany, the Federal Patent Court, and others that were documented by the documentation units of these courts. The documents are anonymized and published in full, and the database is updated daily.
---

## Schema

<ClassContainer name="recht:Decision" class="breakout">
  <ClassContainer name="recht:PrecedingDecision" />
  <ClassContainer name="recht:SubsequentDecision" />
  <ClassContainer name="recht:DecisionPartGuidingPrinciple" />
  <ClassContainer name="recht:DecisionPartHeadnote" />
  <ClassContainer name="recht:DecisionPartOtherHeadnote" />
  <ClassContainer name="recht:DecisionPartOperativePartOfTheJudgment" />
  <ClassContainer name="recht:DecisionPartGroundsOfTheJudgment" />
  <ClassContainer name="recht:DecisionPartFacts" />
  <ClassContainer name="recht:DecisionPartGroundsOfTheDecision" />
  <ClassContainer name="recht:DecisionPartDissentingOpinion" />

  <!-- TODO: Can we find a better way to document these classes? -->
  <ClassContainer name="recht:ObjectionInterimOrder" />
  <ClassContainer name="recht:ValueDeterminationInConstitutionalCourtProceedings" />
  <ClassContainer name="recht:PendingProceeding" />
  <ClassContainer name="recht:ExpertReport" />
  <ClassContainer name="recht:JudgmentByAdmission" />
  <ClassContainer name="recht:InterimOrder" />
  <ClassContainer name="recht:PenaltyNotice" />
  <ClassContainer name="recht:Ruling" />
  <ClassContainer name="recht:Order" />
  <ClassContainer name="recht:DisputeValueDecision" />
  <ClassContainer name="recht:ThreeMemberCommitteeDecision" />
  <ClassContainer name="recht:InterimDecision" />
  <ClassContainer name="recht:CourtDecision" />
  <ClassContainer name="recht:InterimCourtDecision" />
  <ClassContainer name="recht:PartialDecision" />
  <ClassContainer name="recht:ChamberDecision" />
  <ClassContainer name="recht:ChamberDecisionWithoutReasoning" />
  <ClassContainer name="recht:AffirmativeChamberDecision" />
  <ClassContainer name="recht:ArbitrationDecision" />
  <ClassContainer name="recht:LegalAidDecision" />
  <ClassContainer name="recht:TenancyMatterDecision" />
  <ClassContainer name="recht:CostAssessmentDecision" />
  <ClassContainer name="recht:NonAdmittanceDecision" />
  <ClassContainer name="recht:BoardOfAppealDecision" />
  <ClassContainer name="recht:Judgment" />
  <ClassContainer name="recht:FinalJudgment" />
  <ClassContainer name="recht:SupplementaryJudgment" />
  <ClassContainer name="recht:PartialJudgment" />
  <ClassContainer name="recht:DefaultJudgment" />
  <ClassContainer name="recht:PartialDefaultJudgment" />
  <ClassContainer name="recht:WaiverJudgment" />
  <ClassContainer name="recht:SecondDefaultJudgment" />
  <ClassContainer name="recht:InterimJudgment" />
  <ClassContainer name="recht:Disposition" />
  <ClassContainer name="recht:Settlement" />
  <ClassContainer name="recht:Answer" />
  <ClassContainer name="recht:Statement" />
  <ClassContainer name="recht:ReferenceDecision" />
  <ClassContainer name="recht:ECJReference" />
  <ClassContainer name="recht:RequestForPreliminaryRuling" />
  <ClassContainer name="recht:PublicProsecutorsGeneralDecision" />
  <ClassContainer name="recht:PublicProsecutorsOfficeDecision" />
  <ClassContainer name="recht:PublicProsecutorsOfficeDismissalOrder" />
</ClassContainer>

## Endpoints

### List decisions

The endpoint returns a list of decisions from our database. The list is [paginated](/guides/pagination/index.md) and can be [filtered](/guides/filters/index.md) and sorted.

<FeedbackInlineSurvey id="018c252c-e5e6-0000-d66d-acd5f06b9541" context="en-resources-case-law-list-decisions" />

#### Parameters

<RequestParams method="get" path="/api/v1/case-law" />

#### Example

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law
```

:::

<<< @/data/list-decisions.response.json

### Get a single decision

The endpoint returns a single decision from our database.

<FeedbackInlineSurvey id="018c252c-e5e6-0000-d66d-acd5f06b9541" context="en-resources-case-law-get-decision" />

#### Parameters

<RequestParams method="get" path="/api/v1/case-law/{id}" />

#### Example

::: code-group

```bash [cURL]
curl -G https://testphase.rechtsinformationen.bund.de/v1/case-law/ecli/de/bag:2022:201222.u.9azr266.20.0
```

:::

<<< @/data/get-decision.response.json
