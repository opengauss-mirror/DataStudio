@Library('codeflow') _
node("mesos") {
    stage("init") {
        cloudBuildConfig {
            group_id = "gfc0c0a9a63aa4c1191fd6c5007da94f3"
            service_id = "1090009db65f4e628d736650fb30f4a3"
            project_id = "3da32fd2704148a5a153b922c7e583b4"
            project_name = "DataStudio_Main_MR"
            gate_name = "DataStudio_Main_MR"
        }
    }

    cloudBuild {
        jobs = [
            "cmetrics_OpenGauss_master": {
                cloudDragonGate()
            },
            "compile_OpenGauss_master": {
                cloudDragonGate()
            },
            "devtestPresentPropertiesOGauss": {
                cloudDragonGate()
            },
            "devtestPresentTableOGauss": {
                cloudDragonGate()
            },
            "devtest_bl_database_OGauss": {
                cloudDragonGate()
            },
            "devtest_bl_object_OGauss": {
                cloudDragonGate()
            },
            "devtest_bl_table_OGauss": {
                cloudDragonGate()
            },
            "devtest_other_OGauss": {
                cloudDragonGate()
            },
            "findbugs_OpenGauss_master": {
                cloudDragonGate()
            },
            "Reviewbot__OpenGauss_master": {
                cloudDragonGate()
            },
        ]
    }
}

