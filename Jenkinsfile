@Library('codeflow') _
node("mesos") {
    stage("init") {
        cloudBuildConfig {
            group_id = "g69055460842246c480c819e0a327c820"
            service_id = "e79403fcdc114b2cbc1056d60836e1db"
            project_id = "deec770c787445eaa7721ce046f0f1a1"
            project_name = "OpenGauss_DS_MR"
            gate_name = "OpenGauss_DS_MR"
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

