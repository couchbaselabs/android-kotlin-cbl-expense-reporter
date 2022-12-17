package com.couchbase.expensereporter.data.replicator

import com.couchbase.expensereporter.data.DatabaseProvider
import com.couchbase.expensereporter.services.AuthenticationService
import com.couchbase.lite.*
import java.net.URI

class ReplicatorProvider(
    val authenticationService: AuthenticationService
) {
    var replicator: Replicator? = null
    var replicatorConfiguration: ReplicatorConfiguration? = null

    fun setupReplicator(database: Database) {
        //if replicator is already setup stop it before continuing
        replicator?.let { r ->
            r.stop()
            r.close()
        }
        //CHANGE THE URI TO BE THE LOCATION FROM APP SERVICES - IT SHOULD LOOK SOMETHING LIKE THIS:
        //  wss://<yourhostname>.apps.cloud.couchbase.com:4984/expensereports
        val urlEndPoint = URLEndpoint(URI("wss://b-uis7h7-nwrplg.apps.cloud.couchbase.com:4984/expensereports"))
        val currentUser = authenticationService.currentUser.value
        currentUser?.let { user ->
            val basicAuthenticator = BasicAuthenticator(user.username, user.password.toCharArray())
            replicatorConfiguration = ReplicatorConfigurationFactory.create(
                database = database,
                target = urlEndPoint,
                continuous = true,
                authenticator = basicAuthenticator,
                conflictResolver = null,
                type = ReplicatorType.PUSH_AND_PULL
            )
            replicatorConfiguration?.let { config ->
                replicator = Replicator(config)
            }
        }
    }

}