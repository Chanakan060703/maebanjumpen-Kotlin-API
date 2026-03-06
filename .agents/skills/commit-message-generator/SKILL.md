---
name: commit-message-generator
description: Use when you need to generate a conventional commits message from git diff
---

# Commit Message Generator

## Overview
Reads git diff output and generates a commit message following conventional commits format (type: description + body).

## When to Use
- When you have uncommitted changes and need a properly formatted commit message
- When you want consistent commit message style across the project
- When user asks to generate/commit message

## Process

1. **Check for staged or unstaged changes:**
   - Run `git diff --cached` to check staged changes
   - If no staged changes, run `git diff` for unstaged changes

2. **Analyze changed files:**
   - List of files modified/added/deleted
   - Content of changes (added lines, removed lines)

3. **Determine commit type:**
   - `feat`: New feature additions
   - `fix`: Bug fixes
   - `docs`: Documentation changes only
   - `style`: Code style changes (formatting, semicolons, no logic change)
   - `refactor`: Code restructuring without behavior change
   - `test`: Adding/updating tests
   - `chore`: Maintenance, dependencies, tooling, config

4. **Generate message format:**
   ```
   <type>: <short description>

   <body (1-2 lines explaining what and why)>
   ```

## Examples

**Example 1 - New feature:**
```
feat: add user login function

Implement email/password authentication with session management
```

**Example 2 - Bug fix:**
```
fix: resolve null pointer in user profile loading

Handle case where user profile data is missing from database
```

**Example 3 - Documentation:**
```
docs: update API endpoint documentation

Add response schema examples for user endpoints
```

**Example 4 - Refactor:**
```
refactor: simplify authentication flow

Remove redundant token validation steps
```

## Quick Reference

| Change Type | Commit Type |
|-------------|-------------|
| New feature | `feat` |
| Bug fix | `fix` |
| Docs only | `docs` |
| Formatting | `style` |
| Restructure code | `refactor` |
| Tests | `test` |
| Dependencies/tooling | `chore` |

## Output

Show the generated commit message to the user and let them copy it manually. Do NOT auto-commit.
