import { useEffect, useState } from "react";

import MainLayout from "../layouts/MainLayout";
import api from "../api/axios";
import AddWaterSourceForm from "../components/AddWaterSourceForm";

function WaterSourcesPage() {

    const [sources, setSources] = useState([]);

    useEffect(() => {
        fetchSources();
    }, []);

    const fetchSources = async () => {

        try {

            const response =
                await api.get("/water-sources");

            setSources(response.data);

        } catch (error) {

            console.error(error);
        }
    };

    return (

        <MainLayout>

            <header className="page-header">
                <h1 className="page-title">Water Sources</h1>
            </header>

            <AddWaterSourceForm onSuccess={fetchSources} />
            <TableContainer title="Available Sources">
                <div className="list-grid">
                    {sources.map(source => (

                        <div
                            key={source.id}
                            className="list-card"
                        >

                            <h2 className="list-card__title">{source.name}</h2>

                            <p>
                                Pricing Type:
                                {" "}
                                {source.pricingType}
                            </p>

                            <p>
                                Supply Type:
                                {" "}
                                {source.supplyType}
                            </p>

                        </div>
                    ))}
                </div>
            </TableContainer>

        </MainLayout>
    );
}

export default WaterSourcesPage;
