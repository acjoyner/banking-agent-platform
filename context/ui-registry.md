# UI Registry & Styling Tokens

## Design System Baseline

Established: 2026-07-24

| Property | CSS / Token Value | Description |
| :--- | :--- | :--- |
| **Main Background** | `--bg-main: #0B0F19` | Deep dark obsidian canvas |
| **Base Text Color** | `--text-main: #F3F4F6` | Off-white high contrast readable text |
| **Muted Text Color** | `--text-muted: #9CA3AF` | Medium gray for secondary labels/descriptions |
| **Primary Theme Accent** | `--color-primary: #10B981` | Emerald green representing cash flow and success |
| **Glassmorphism Panels** | `.glass-panel` | `background: rgba(17, 24, 39, 0.65)` with `backdrop-filter: blur(12px)` |
| **Translucent Border** | `--border-subtle: rgba(255, 255, 255, 0.08)` | Thin border divider for panels |
| **Primary Button** | `.btn-primary` | Linear gradient emerald green with shadow glow |
| **Secondary Button** | `.btn-secondary` | Translucent background with subtle border |
| **Entry Animations** | `.fade-in`, `.slide-up` | Basic entry transitions for components |

---

## Component Registries

### Global Layout & Containers
File: `src/app/layout.tsx`
*   **Body Class**: `min-h-full flex flex-col`
*   **Global Class**: `h-full antialiased`
*   **Fonts**: `Geist` (sans), `Geist_Mono` (mono)

### Custom Colors & Badges
*   **Glow Card Green**: `border: 1px solid var(--border-glow);` - for low-risk items.
*   **Glow Card Amber**: `border: 1px solid rgba(245, 158, 11, 0.2);` - for medium-risk items.
*   **Glow Card Red**: `border: 1px solid rgba(239, 68, 68, 0.2);` - for high-risk alerts.
