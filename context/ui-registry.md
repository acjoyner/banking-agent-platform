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

### Interactive Dashboard Page
File: `src/app/page.tsx`
Last updated: 2026-07-24

| Property | Style / Classes | Description |
| :--- | :--- | :--- |
| **Grid Layout** | `grid grid-cols-1 md:grid-cols-3` | 3-column top card row for selectors/balances |
| **Console log** | `bg-black/60 border border-white/10 text-emerald-400` | Mock terminal log display |
| **Floating Chat** | `w-80 h-96 fixed bottom-6 right-6 border-[#10B981]` | Advisory widget container |
| **Table Layout** | `w-full text-left border-collapse font-mono` | Real-time transaction history display |

