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

            <h1>Water Sources</h1>
            <AddWaterSourceForm onSuccess={fetchSources} />
            <h1>Available Sources</h1>
            {sources.map(source => (

                <div
                    key={source.id}
                    style={{
                        backgroundColor: "#1b1b1b",
                        padding: "15px",
                        borderRadius: "12px",
                        marginBottom: "10px",
                        border: "1px solid #333"
                    }}
                >

                    <h2>{source.name}</h2>

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

        </MainLayout>
    );
}

export default WaterSourcesPage;