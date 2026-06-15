import { Routes, Route, Navigate } from "react-router-dom";

import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import DailyLogsPage from "./pages/DailyLogsPage";
import WaterSourcesPage from "./pages/WaterSourcesPage";
import WaterRatesPage from "./pages/WaterRatesPage";
import ApartmentsPage from "./pages/ApartmentsPage";
import ApartmentTypesPage from "./pages/ApartmentTypesPage";
import BlocksPage from "./pages/BlocksPage";
import SourceConfigsPage from "./pages/SourceConfigsPage";
import MonthlySummaryPage from "./pages/MonthlySummaryPage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
    return (
        <Routes>

            <Route
                path="/login"
                element={<LoginPage />}
            />

            <Route
                path="/"
                element={<Navigate to="/dashboard" replace />}
            />

            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute>
                        <DashboardPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/logs"
                element={
                    <ProtectedRoute>
                        <DailyLogsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/sources"
                element={
                    <ProtectedRoute>
                        <WaterSourcesPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/rates"
                element={
                    <ProtectedRoute>
                        <WaterRatesPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/apartments"
                element={
                    <ProtectedRoute>
                        <ApartmentsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/apartment-types"
                element={
                    <ProtectedRoute>
                        <ApartmentTypesPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/blocks"
                element={
                    <ProtectedRoute>
                        <BlocksPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/source-configs"
                element={
                    <ProtectedRoute>
                        <SourceConfigsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/monthly-summary"
                element={
                    <ProtectedRoute>
                        <MonthlySummaryPage />
                    </ProtectedRoute>
                }
            />

        </Routes>
    );
}

export default App;