## Summary

<!-- What changed and why. Reference the task: Refs P<NN>-T<NN> -->

## Type

- [ ] feat / fix / refactor / test / docs / chore / perf / build / ci
- [ ] Breaking change (`BREAKING CHANGE:` noted in the commit body)

## Checklist (Definition of Done)

- [ ] Branch targets `develop` (see `TDOP-docs/DEVELOPMENT_GUIDE.md` §6)
- [ ] Follows `TDOP-docs/CODING_STANDARDS.md` (layering, naming, no dead code)
- [ ] Tests written/updated and green (`mvn test` / `npm test`)
- [ ] No secrets, `.env` values, or credentials anywhere in the diff
- [ ] User-visible strings added to both `en.json` and `sw.json` (if applicable)
- [ ] Security standards respected for touched areas (`SECURITY_STANDARDS.md`)
- [ ] README / `CHANGELOG.md` (per project part) / `PROJECT_MANAGEMENT.md` session updated

## Board state

Move the task to `CODE REVIEW` in `PROJECT_MANAGEMENT.md` + `TASK_BREAKDOWN.md`
when opening this PR (and mirror it on the Kanban board).
