# PSW Mobile Design Specification

## 1. Overview

PSW Mobile is an Android application built with Jetpack Compose that serves as a comprehensive platform for managing company data and business operations. The application follows Material Design 3 principles and provides a clean, modern interface for users to interact with company databases.

## 2. Color Scheme & Theme

### 2.1 Primary Color Scheme
The application uses Material Theme 3's default light color scheme with the following characteristics:

- **Theme System**: Material Design 3 Light Color Scheme
- **Primary Colors**: Material Theme default (typically blue variants)
- **Color Implementation**: `lightColorScheme()` from Compose Material 3

### 2.2 Custom Color Implementations
The app uses specific custom colors for status indicators and charts:

#### Status Colors:
- **Active Status**: `#4CAF50` (Green) with 10% alpha background
- **Inactive Status**: `#F44336` (Red) with 10% alpha background  
- **Pending Status**: `#FF9800` (Orange) with 10% alpha background
- **Chart Primary**: `#2196F3` (Blue)

#### Color Usage Patterns:
- Primary text uses `MaterialTheme.colorScheme.primary`
- Error states use `MaterialTheme.colorScheme.error`
- Surface variants use `MaterialTheme.colorScheme.onSurfaceVariant`
- Backgrounds follow Material 3 surface hierarchy

## 3. Typography System

### 3.1 Font Specifications
The application uses the default system font with the following size hierarchy:

#### Heading Levels:
- **App Title**: 32sp, FontWeight.Bold
- **Screen Titles**: 24sp, FontWeight.Bold
- **Card Titles**: 18sp, FontWeight.SemiBold
- **Section Headers**: 18sp, FontWeight.SemiBold

#### Body Text:
- **Primary Body**: 16sp, regular weight
- **Secondary Body**: 14sp, regular weight
- **Caption Text**: 12sp, regular weight
- **Small Text**: 10sp, regular weight

#### Interactive Elements:
- **Button Text**: Default Material 3 button typography
- **TextField Labels**: Default Material 3 text field typography
- **Navigation Text**: Default Material 3 navigation typography

### 3.2 Font Weight Usage:
- **FontWeight.Bold**: Main app title, primary headings
- **FontWeight.SemiBold**: Screen titles, card headers, important labels
- **FontWeight.Medium**: Company names, key values
- **FontWeight.Regular**: Body text, descriptions, general content

## 4. Screen-by-Screen Design Specification

### 4.1 Login Screen (`LoginScreen.kt`)

#### Layout Structure:
- **Container**: Full-screen Column with center alignment
- **Padding**: 32dp all around
- **Arrangement**: Center vertical arrangement

#### Components:

##### App Title
- **Text**: "PSW Mobile"
- **Font Size**: 32sp
- **Font Weight**: Bold
- **Color**: MaterialTheme.colorScheme.primary
- **Bottom Margin**: 48dp

##### Username Field
- **Component**: OutlinedTextField
- **Width**: Fill parent width
- **Bottom Margin**: 16dp
- **Label**: "Username"
- **Keyboard Type**: Text

##### Password Field
- **Component**: OutlinedTextField
- **Width**: Fill parent width
- **Bottom Margin**: 24dp
- **Label**: "Password"
- **Input Type**: Password (masked)
- **Keyboard Type**: Password

##### Error Display
- **Text Color**: MaterialTheme.colorScheme.error
- **Bottom Margin**: 16dp
- **Visibility**: Conditional (only when error state)

##### Login Button
- **Component**: Material Button
- **Width**: Fill parent width
- **Height**: 48dp
- **Content**: "Login" text or CircularProgressIndicator (20dp)
- **State Management**: Disabled during loading

##### Biometric Login Button
- **Component**: OutlinedButton
- **Width**: Fill parent width
- **Height**: 48dp
- **Icon**: Fingerprint icon (8dp end padding)
- **Text**: "🔐 Biometric Login"
- **Top Margin**: 16dp

### 4.2 Dashboard Screen (`DashboardScreen.kt`)

#### Layout Structure:
- **Container**: Column with TopAppBar + LazyColumn
- **Content Padding**: 16dp
- **Item Spacing**: 16dp

#### TopAppBar:
- **Title**: "PSW Dashboard"
- **Actions**: Refresh icon, Logout icon
- **Style**: Material 3 TopAppBar

#### Content Sections:

##### Welcome Section
- **Primary Heading**: "Welcome to PSW Mobile" (24sp, Bold)
- **Subtitle**: "Manage your companies efficiently" (16sp, onSurfaceVariant)
- **Bottom Margin**: 24dp

##### Navigation Cards
Each card follows the `DashboardCard` component pattern:
- **Card Elevation**: 4dp
- **Padding**: 16dp
- **Icon Size**: 48dp with 16dp end padding
- **Icon Color**: MaterialTheme.colorScheme.primary
- **Title**: 18sp, SemiBold
- **Description**: 14sp, onSurfaceVariant

###### Master List Card
- **Icon**: Business icon
- **Title**: "Master List"
- **Description**: "View and manage all companies"

###### New Companies Card
- **Icon**: Add icon
- **Title**: "New Companies"
- **Description**: "Review newly added companies"

##### Quick Stats Card
- **Card Elevation**: 4dp
- **Padding**: 16dp
- **Title**: "Quick Stats" (18sp, SemiBold, 12dp bottom margin)

###### Stat Items Layout:
- **Value Text**: 20sp, Bold, primary color
- **Label Text**: 12sp, onSurfaceVariant
- **Alignment**: Center horizontal
- **Arrangement**: SpaceBetween in rows

##### Charts Section

###### Pie Chart Card
- **Title**: "Company Status Distribution" (18sp, SemiBold)
- **Chart Size**: Full width, 200dp height
- **Legend**: Right-aligned with color indicators (16dp size, 2dp radius)
- **Colors**: Active (Green), Inactive (Red), Pending (Orange)

###### Bar Chart Card
- **Title**: "Weekly New Companies" (18sp, SemiBold)
- **Chart Size**: Full width, 200dp height
- **Bar Color**: #2196F3
- **X-axis Labels**: Day abbreviations with values above

###### Recent Companies Table
- **Title**: "Recent Companies" (18sp, SemiBold)
- **Header Background**: surfaceVariant
- **Header Padding**: 12dp
- **Row Padding**: 12dp
- **Status Chips**: Rounded background with status colors
- **Dividers**: outline color with 20% alpha, 0.5dp thickness

### 4.3 Master List Screen (`MasterlistScreen.kt`)

#### Layout Structure:
- **Container**: Column with TopAppBar + search + content
- **Content Padding**: 16dp

#### TopAppBar:
- **Title**: "Master List"
- **Navigation**: Back arrow
- **Actions**: Refresh icon

#### Search Section:
- **Component**: OutlinedTextField
- **Width**: Fill parent width
- **Bottom Margin**: 16dp
- **Label**: "Search companies..."
- **Leading Icon**: Search icon

#### Results Display:
- **Count Text**: 14sp, onSurfaceVariant, 8dp bottom margin
- **List Spacing**: 8dp between items

##### Company Cards (`CompanyCard`):
- **Card Elevation**: 2dp
- **Padding**: 16dp
- **Company Name**: 16sp, SemiBold
- **Status Chip**: Custom Surface with status colors
- **Industry**: 14sp, primary color
- **Location**: 14sp, onSurfaceVariant
- **Spacing**: 8dp between name and details

##### Status Chips (`StatusChip`):
- **Background**: Status color with 10% alpha
- **Text Color**: Full status color
- **Font Size**: 12sp
- **Padding**: 8dp horizontal, 4dp vertical
- **Shape**: MaterialTheme.shapes.small
- **Margin**: 4dp

### 4.4 New Companies Screen (`NewCompaniesScreen.kt`)

#### Layout Structure:
- **Container**: Column with TopAppBar + filters + content
- **Content Padding**: 16dp

#### TopAppBar:
- **Title**: "New Companies" 
- **Navigation**: Back arrow
- **Actions**: Refresh icon

#### Filter Section:

##### Search Bar:
- **Component**: OutlinedTextField
- **Width**: Fill parent width
- **Bottom Margin**: 8dp
- **Label**: "Search companies..."
- **Leading Icon**: Search icon
- **Single Line**: true

##### Filter Controls:
- **Filter Header**: Row with Filter icon + "Filters:" (14sp)
- **Control Spacing**: 8dp between elements

##### Status Filter:
- **Label**: "Status:" (12sp, 60dp width)
- **Component**: OutlinedButton with dropdown
- **Options**: Pending, Bought, Blocked, No, All
- **Font Size**: 12sp

##### Broker & Country Filters:
- **Layout**: Row with equal weights
- **Labels**: 12sp above each dropdown
- **Components**: OutlinedButton with dropdown menus
- **Font Size**: 12sp
- **Spacing**: 8dp between broker and country

#### Content Display:
- **Count Text**: 14sp, onSurfaceVariant, 16dp bottom margin
- **List Spacing**: 12dp between items

##### New Company Cards (`NewCompanyCard`):
- **Card Elevation**: 4dp
- **Padding**: 16dp

###### Header Row:
- **Company Name**: 18sp, SemiBold
- **NEW Badge**: Secondary color background (10% alpha), 12sp Bold

###### Description:
- **Font Size**: 14sp
- **Color**: onSurfaceVariant
- **Bottom Margin**: 12dp

###### Info Sections:
- **Layout**: Two columns with SpaceBetween arrangement
- **Info Rows**: Label (12sp, onSurfaceVariant, 80dp width) + Value (12sp, Medium)

## 5. Icons & Visual Assets

### 5.1 Material Icons Used

#### Navigation & Actions:
- **Icons.Default.ArrowBack**: Back navigation
- **Icons.Default.Refresh**: Refresh actions
- **Icons.Default.ExitToApp**: Logout functionality
- **Icons.Default.Search**: Search functionality
- **Icons.Default.FilterList**: Filter controls

#### Functional Icons:
- **Icons.Default.Business**: Master list/companies
- **Icons.Default.Add**: New companies/additions
- **Icons.Default.Fingerprint**: Biometric authentication
- **Icons.Default.Check**: Approval actions
- **Icons.Default.Close**: Rejection/close actions

### 5.2 Icon Specifications:
- **Standard Size**: 24dp (Material default)
- **Large Icons**: 48dp (dashboard cards)
- **Small Icons**: 20dp (loading indicators)
- **Button Icons**: 16dp with 8dp padding

### 5.3 Color Applications:
- **Primary Actions**: MaterialTheme.colorScheme.primary
- **Navigation**: Default Material colors
- **Status Indicators**: Custom status colors

## 6. Component Specifications

### 6.1 Cards
- **Default Elevation**: 4dp (primary cards), 2dp (list items)
- **Shape**: Material 3 default card shape
- **Padding**: 16dp internal padding
- **Margins**: 16dp between major cards, 8-12dp between list items

### 6.2 Buttons
- **Primary Button Height**: 48dp
- **Button Width**: Fill parent width (forms), wrap content (actions)
- **Text Style**: Material 3 button defaults

### 6.3 Text Fields
- **Component**: OutlinedTextField (consistent across app)
- **Width**: Fill parent width
- **Margins**: 16dp bottom for form fields, 8dp for filters

### 6.4 Loading States
- **Indicator**: Material CircularProgressIndicator
- **Size**: 20dp (buttons), default (full screen)
- **Color**: onPrimary (buttons), primary (screens)

### 6.5 Error States
- **Text Color**: MaterialTheme.colorScheme.error
- **Background**: None (text only)
- **Typography**: Same as surrounding content

## 7. Layout Patterns

### 7.1 Screen Structure:
1. **TopAppBar** (when applicable)
2. **Search/Filter Section** (when applicable)  
3. **Content Area** (LazyColumn for lists, Column for forms)

### 7.2 Spacing System:
- **Screen Margins**: 16dp horizontal, 32dp for login
- **Component Spacing**: 8dp (tight), 16dp (normal), 24dp (loose)
- **Section Spacing**: 24dp-48dp between major sections

### 7.3 Responsive Behavior:
- **Width**: Fill parent width for most components
- **Height**: Wrap content with specific heights for buttons (48dp)
- **Alignment**: Center for forms, start for lists

## 8. Interaction Patterns

### 8.1 Navigation:
- **Back Navigation**: Arrow icon in TopAppBar
- **Forward Navigation**: Card tap, button press
- **Tab Navigation**: Not implemented

### 8.2 Form Interactions:
- **Text Input**: OutlinedTextField with proper keyboard types
- **Button States**: Enabled/disabled based on input validation
- **Loading States**: Button shows spinner during processing

### 8.3 List Interactions:
- **Filtering**: Real-time search + dropdown filters
- **Refresh**: Pull-to-refresh via refresh button
- **Selection**: Card tap (implied)

## 9. Animation & Transitions

### 9.1 Current Implementation:
- **Material 3 Defaults**: Standard Material motion
- **No Custom Animations**: Relies on Compose defaults
- **State Changes**: Smooth transitions via Compose state management

### 9.2 Loading Animations:
- **CircularProgressIndicator**: Standard Material loading spinner
- **Button Loading**: Replaces text with spinner during processing

## 10. Accessibility Considerations

### 10.1 Content Descriptions:
- **Icons**: Proper contentDescription for all icons
- **Interactive Elements**: Clear descriptions for buttons and actions
- **Navigation**: Back button and refresh actions properly labeled

### 10.2 Color Contrast:
- **Material 3 Compliance**: Default color scheme ensures proper contrast
- **Status Colors**: Custom colors maintain sufficient contrast ratios
- **Text Readability**: Appropriate color choices for all text elements

## 11. Technical Implementation Notes

### 11.1 Theme Implementation:
- **File**: `android/app/src/main/java/com/psw/mobile/ui/theme/Theme.kt`
- **Structure**: Single PSWMobileTheme composable
- **Color Scheme**: Material 3 lightColorScheme()

### 11.2 Screen Files:
- **LoginScreen.kt**: Authentication interface
- **DashboardScreen.kt**: Main navigation and statistics
- **MasterlistScreen.kt**: Company listing and search
- **NewCompaniesScreen.kt**: New company review with filtering

### 11.3 Reusable Components:
- **DashboardCard**: Navigation cards with icon, title, description
- **CompanyCard**: Company information display
- **NewCompanyCard**: New company detailed view
- **StatusChip**: Status indicator with color coding
- **StatItem**: Statistics display with value and label
- **InfoRow**: Key-value information display

This comprehensive design specification covers all visual, typographic, and interactive elements of the PSW Mobile application, providing a complete reference for design consistency and implementation standards.