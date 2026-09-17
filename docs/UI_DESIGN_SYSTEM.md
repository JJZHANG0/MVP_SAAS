# ToolFix UI Design System

## Overview

ToolFix implements a **premium, systematic design system** inspired by industry leaders like Linear, Stripe, and Vercel. The design emphasizes:

- **Cohesive visual language** across Admin and H5
- **Strategic glassmorphism** (frosted glass effects)
- **Micro-interactions** for delightful user experience
- **Semantic color system** for clear status communication
- **Accessibility** and performance optimization

## Design Tokens

All design tokens are defined as CSS variables for consistency and maintainability.

### Color Palette

#### Brand
- **Primary**: `#6366f1` (Indigo 500)
- **Primary Hover**: `#4f46e5` (Indigo 600)
- **Primary Light**: `#818cf8` (Indigo 400)

#### Neutrals (10-level grayscale)
- 50: `#fafafa` (backgrounds)
- 100-300: Light borders and subtle fills
- 400-600: Text secondary/tertiary
- 700-900: Primary text and dark surfaces

#### Semantic Colors
- **Success**: `#10b981` (Green 500)
- **Warning**: `#f59e0b` (Amber 500)
- **Error**: `#ef4444` (Red 500)
- **Info**: `#3b82f6` (Blue 500)
- **Hazard**: `#dc2626` (Red 600)

### Spacing System (4px grid)

```
--space-1:  4px
--space-2:  8px
--space-3:  12px
--space-4:  16px
--space-5:  20px
--space-6:  24px
--space-8:  32px
--space-10: 40px
--space-12: 48px
--space-16: 64px
```

### Typography

#### Size Scale
```
--text-xs:   12px
--text-sm:   13px
--text-base: 14px
--text-lg:   16px
--text-xl:   18px
--text-2xl:  24px
--text-3xl:  30px (Admin) / 28px (H5)
```

#### Weight Scale
```
--font-normal:    400
--font-medium:    500
--font-semibold:  600
--font-bold:      700
```

#### Line Heights
```
--line-tight:   1.25
--line-normal:  1.5
--line-relaxed: 1.75
```

### Border Radius

```
--radius-sm:   6px
--radius-md:   8px
--radius-lg:   12px
--radius-xl:   16px (Admin) / 20px (H5)
--radius-full: 9999px (pills/badges)
```

### Shadows (Layered, soft)

```
--shadow-xs:  0 1px 2px rgba(0, 0, 0, 0.04)
--shadow-sm:  0 2px 4px rgba(0, 0, 0, 0.04), 0 1px 2px rgba(0, 0, 0, 0.02)
--shadow-md:  0 4px 8px rgba(0, 0, 0, 0.04), 0 2px 4px rgba(0, 0, 0, 0.02)
--shadow-lg:  0 8px 16px rgba(0, 0, 0, 0.06), 0 4px 8px rgba(0, 0, 0, 0.03)
--shadow-xl:  0 16px 32px rgba(0, 0, 0, 0.08), 0 8px 16px rgba(0, 0, 0, 0.04)
```

### Blur Levels

```
Admin (desktop performance):
--blur-sm: 8px
--blur-md: 16px
--blur-lg: 24px

H5 (mobile optimized):
--blur-sm: 8px
--blur-md: 12px  (lighter for scroll performance)
```

### Transitions

```
--transition-fast: 150ms cubic-bezier(0.4, 0, 0.2, 1)
--transition-base: 200ms cubic-bezier(0.4, 0, 0.2, 1)
--transition-slow: 300ms cubic-bezier(0.4, 0, 0.2, 1)
```

## Glassmorphism (磨砂玻璃)

### Where It's Applied

#### Admin Console

1. **Sidebar** (`admin/src/layouts/MainLayout.vue`)
   - Background: `rgba(255, 255, 255, 0.7)`
   - Backdrop filter: `blur(16px) saturate(180%)`
   - Border: `1px solid rgba(0, 0, 0, 0.06)`
   - Shadow: Soft 8px blur with 4% opacity

2. **Top Header** (`admin/src/layouts/MainLayout.vue`)
   - Same glass properties as sidebar
   - Fixed positioning for scroll-over effect

3. **Stat Cards** (`admin/src/views/Dashboard.vue` - optional variant)
   - Can be configured with `.glass` class
   - Gradient backgrounds with translucent overlay

#### H5 Mobile

1. **NavBar** (`vant-overrides.css`)
   - Background: `rgba(255, 255, 255, 0.85)`
   - Backdrop filter: `blur(12px) saturate(180%)`
   - Lighter blur for mobile performance

2. **Chat Input Area** (`h5/src/views/Diagnosis.vue`)
   - Background: `rgba(255, 255, 255, 0.85)`
   - Backdrop filter: `blur(12px) saturate(180%)`
   - Fixed bottom positioning
   - Safe area padding for iOS notch

### Fallback Strategy

All glassmorphism uses `@supports` feature detection:

```css
.glass {
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  box-shadow: var(--glass-shadow);
}

@supports (backdrop-filter: blur(var(--blur-md))) {
  .glass {
    backdrop-filter: blur(var(--blur-md)) saturate(180%);
  }
}

@supports not (backdrop-filter: blur(var(--blur-md))) {
  .glass {
    background: rgba(255, 255, 255, 0.98); /* More opaque fallback */
  }
}
```

## Micro-Interactions

### Hover Effects

#### Cards
```css
.card {
  transition: all var(--transition-base);
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
  border-color: var(--neutral-300);
}
```

#### Stat Cards
```css
.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-xl);
}
```

#### Buttons
```css
.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.btn-primary:active {
  transform: scale(0.98);
}
```

### Animations

#### Message Slide-In (H5 Chat)
```css
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item {
  animation: slideIn 0.2s ease-out;
}
```

#### Hazard Pulse (H5 Chat)
```css
@keyframes pulse {
  0%, 100% { 
    box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2); 
  }
  50% { 
    box-shadow: 0 4px 16px rgba(220, 38, 38, 0.4); 
  }
}

.message-hazard .message-bubble {
  animation: pulse 1s ease-in-out;
}
```

#### Brand Icon Shimmer (Admin Sidebar)
```css
@keyframes shimmer {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-10%, -10%); }
}

.sidebar-header::before {
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  animation: shimmer 3s ease-in-out infinite;
}
```

## Status Badges

Consistent semantic badges across Admin and H5:

```css
.badge {
  display: inline-flex;
  align-items: center;
  padding: var(--space-1) var(--space-3);
  border-radius: var(--radius-full);
  font-size: var(--text-xs);
  font-weight: var(--font-semibold);
  line-height: 1;
  border: 1px solid transparent;
}

/* Success */
.badge-success {
  background: var(--color-success-bg);  /* #d1fae5 */
  color: var(--color-success);          /* #10b981 */
  border-color: rgba(16, 185, 129, 0.2);
}

/* Warning */
.badge-warning {
  background: var(--color-warning-bg);  /* #fef3c7 */
  color: #b45309;
  border-color: rgba(245, 158, 11, 0.2);
}

/* Error/Danger */
.badge-error {
  background: var(--color-error-bg);    /* #fee2e2 */
  color: var(--color-error);            /* #ef4444 */
  border-color: rgba(239, 68, 68, 0.2);
}

/* Info */
.badge-info {
  background: var(--color-info-bg);     /* #dbeafe */
  color: var(--color-info);             /* #3b82f6 */
  border-color: rgba(59, 130, 246, 0.2);
}

/* Hazard (H5 specific) */
.badge-hazard {
  background: var(--color-hazard-bg);   /* #fef2f2 */
  color: var(--color-hazard);           /* #dc2626 */
  border-color: rgba(220, 38, 38, 0.3);
}
```

## Component Library Overrides

### Element Plus (Admin)

Theme tokens defined in `admin/src/styles/element-overrides.css`:

- **Primary color**: `#6366f1` (replaces default blue)
- **Success/Warning/Danger/Info**: Aligned with design system
- **Border radius**: 8px (base), 6px (small), 9999px (round)
- **Shadows**: Layered soft shadows
- **Typography**: Noto Sans SC, Microsoft YaHei for Chinese

Key component overrides:
- **Cards**: 12px radius, hover lift, enhanced shadows
- **Buttons**: Rounded, weight 500, smooth transitions
- **Tables**: Uppercase headers, hover states, clean borders
- **Tags**: Semantic colors, rounded, bold weights
- **Menu**: Clean borders, rounded items, smooth transitions
- **Inputs**: 8px radius, focus rings with primary color
- **Dialog**: 12px radius, blurred overlay

### Vant 4 (H5)

Theme tokens defined in `h5/src/styles/vant-overrides.css`:

- **Primary color**: `#6366f1`
- **Border radius**: 12px (buttons/fields), 16px (dialogs)
- **Touch targets**: 48px minimum (accessibility)

Key component overrides:
- **NavBar**: Glass effect, 17px bold title
- **Buttons**: Gradient primary, 12px radius, 600 weight
- **Fields**: Rounded, focus states with primary color
- **Dialog**: 16px radius, blurred overlay
- **Uploader**: Rounded previews, dashed borders
- **Tags**: Semantic colors matching Admin

## Key Pages to Review

### Admin Console

1. **Dashboard** (`/`)
   - Gradient stat cards with hover lift
   - Recent sessions table with badges
   - Quick action buttons
   - Glass sidebar/header

2. **Sessions List** (`/sessions`)
   - Filterable table
   - Confidence progress bars
   - Status/outcome badges
   - Hazard indicators

3. **Session Detail** (`/sessions/:id`)
   - Full conversation transcript
   - Message bubbles (user vs AI)
   - Confidence visualization
   - Human chat takeover interface

4. **Manuals** (`/manuals`)
   - Upload interface
   - Parsing progress
   - Locked state indicators

5. **Knowledge Base** (`/knowledge`)
   - Scenario cards
   - Detail modals

### H5 Mobile

1. **Diagnosis Chat** (`/diagnosis/:sessionUuid`)
   - Glass input bar
   - Premium chat bubbles
   - Hazard warning with pulse
   - Round counter (1/5)

2. **Guide Pages** (`/guide/:slug`)
   - Gradient header with shimmer
   - Step cards with numbered badges
   - Media placeholders
   - Feedback buttons

## Performance Considerations

### Admin (Desktop)
- Full 16px blur for glassmorphism (desktop GPUs handle well)
- Layered shadows (multiple box-shadow values)
- Smooth 200-300ms transitions

### H5 (Mobile)
- Lighter 12px blur (reduces scroll jank)
- Simplified shadows (single box-shadow)
- Fast 150-200ms transitions
- `-webkit-tap-highlight-color: transparent` (cleaner touch)
- Optimized for 60fps scrolling

## Accessibility

- **Focus states**: 2px solid primary with 2px offset
- **Color contrast**: All text meets WCAG AA (4.5:1 minimum)
- **Touch targets**: 48px minimum on H5
- **Semantic HTML**: Proper heading hierarchy
- **ARIA labels**: Where needed (icon buttons, etc.)

## Files Reference

### Admin
- `admin/src/styles/theme.css` - Core design tokens
- `admin/src/styles/element-overrides.css` - Element Plus theming
- `admin/src/style.css` - Global utilities
- `admin/src/layouts/MainLayout.vue` - Glass sidebar/header
- `admin/src/views/Dashboard.vue` - Stat cards implementation

### H5
- `h5/src/styles/theme.css` - Core design tokens (mobile)
- `h5/src/styles/vant-overrides.css` - Vant 4 theming
- `h5/src/style.css` - Global utilities
- `h5/src/views/Diagnosis.vue` - Glass input bar, chat bubbles
- `h5/src/views/Guide.vue` - Guide page layout

## Design Philosophy

### "高级、系统" (Premium & Systematic)

1. **Restrained elegance**: Avoid loud gradients, use subtle glass effects
2. **Consistent spacing**: 4px grid everywhere
3. **Purposeful animation**: Every transition serves UX, not decoration
4. **Semantic clarity**: Color = meaning (green = success, red = danger)
5. **Performance first**: Optimize blur for mobile, maintain 60fps
6. **Accessible by default**: High contrast, clear focus states

### Inspiration Sources

- **Linear**: Clean sidebar, soft shadows, Indigo brand color
- **Stripe**: Refined typography, semantic colors, card elevation
- **Vercel**: Dark-leaning neutrals, systematic spacing, minimal borders

## Future Enhancements

- [ ] Dark mode variant (use CSS `prefers-color-scheme`)
- [ ] Custom illustration system (replace icon placeholders)
- [ ] Advanced data visualizations (charts, graphs)
- [ ] Skeleton loading states
- [ ] Toast/notification system refinement
- [ ] More animation variety (stagger, spring physics)
