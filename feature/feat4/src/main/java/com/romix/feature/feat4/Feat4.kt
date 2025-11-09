package com.romix.feature.feat4

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat4Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat4UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat4FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat4UserSummary
)

data class Feat4UiModel(
    val header: com.romix.core.ui.UiText,
    val items: List<com.romix.core.ui.UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat4NetworkSnapshot(
    val users: List<com.romix.core.network.ApiUserDto>,
    val posts: List<com.romix.core.network.ApiPostDto>,
    val rawHash: Int
)

class Feat4Repository(
    private val api: com.romix.core.network.FakeApiService = com.romix.core.network.FakeApiService(
        com.romix.core.network.FakeNetworkClient()
    ),
    private val config: Feat4Config = Feat4Config()
) {

    fun loadSnapshot(userId: Long): Feat4NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat4NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: com.romix.core.network.ApiUserDto, posts: List<com.romix.core.network.ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: com.romix.core.model.CoreUser): Feat4UserSummary {
        return Feat4UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = com.romix.core.model.computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<com.romix.core.model.CoreUser>): List<Feat4FeedItem> {
        val result = java.util.ArrayList<Feat4FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat4FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat4UiMapper {

    fun mapToUi(model: List<Feat4FeedItem>): Feat4UiModel {
        val items = model.mapIndexed { index, item ->
            com.romix.core.ui.UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat4UiModel(
            header = com.romix.core.ui.UiText("Feat4 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat4UiModel =
        Feat4UiModel(
            header = com.romix.core.ui.UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat4UiModel =
        Feat4UiModel(
            header = com.romix.core.ui.UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat4UiModel =
        Feat4UiModel(
            header = com.romix.core.ui.UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat4Service(
    private val repository: Feat4Repository,
    private val uiMapper: Feat4UiMapper,
    private val networkClient: com.romix.core.network.FakeNetworkClient,
    private val apiService: com.romix.core.network.FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat4UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = com.romix.core.model.CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = com.romix.core.network.NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat4UiModel {
        val users = (0 until usersCount).map {
            com.romix.core.model.CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat4UserItem1(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem2(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem3(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem4(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem5(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem6(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem7(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem8(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem9(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat4UserItem10(val user: com.romix.core.model.CoreUser, val label: String)

data class Feat4StateBlock1(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock2(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock3(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock4(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock5(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock6(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock7(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock8(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock9(val state: Feat4UiModel, val checksum: Int)
data class Feat4StateBlock10(val state: Feat4UiModel, val checksum: Int)

fun buildFeat4UserItem(user: com.romix.core.model.CoreUser, index: Int): Feat4UserItem1 {
    return Feat4UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat4StateBlock(model: Feat4UiModel): Feat4StateBlock1 {
    return Feat4StateBlock1(
        state = model,
        checksum = com.romix.core.model.computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<com.romix.core.model.CoreUser>): List<Feat4UserSummary> {
    val list = java.util.ArrayList<Feat4UserSummary>(users.size)
    for (user in users) {
        list += Feat4UserSummary(
            id = user.id,
            name = user.name,
            checksum = com.romix.core.model.computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat4UserSummary>): List<com.romix.core.ui.UiListItem> {
    val items = java.util.ArrayList<com.romix.core.ui.UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += com.romix.core.ui.UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat4UiModel {
    val summaries = (0 until count).map {
        Feat4UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat4UiModel(
        header = com.romix.core.ui.UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<com.romix.core.model.CoreUser> {
    val list = java.util.ArrayList<com.romix.core.model.CoreUser>(count)
    for (i in 0 until count) {
        list += com.romix.core.model.CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<com.romix.core.model.CoreUser>): List<com.romix.core.ui.UiText> {
    val list = java.util.ArrayList<com.romix.core.ui.UiText>(users.size)
    for (user in users) {
        list += com.romix.core.ui.UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat4UiModel> {
    val models = java.util.ArrayList<Feat4UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat4AnalyticsEvent1(val name: String, val value: String)
data class Feat4AnalyticsEvent2(val name: String, val value: String)
data class Feat4AnalyticsEvent3(val name: String, val value: String)
data class Feat4AnalyticsEvent4(val name: String, val value: String)
data class Feat4AnalyticsEvent5(val name: String, val value: String)
data class Feat4AnalyticsEvent6(val name: String, val value: String)
data class Feat4AnalyticsEvent7(val name: String, val value: String)
data class Feat4AnalyticsEvent8(val name: String, val value: String)
data class Feat4AnalyticsEvent9(val name: String, val value: String)
data class Feat4AnalyticsEvent10(val name: String, val value: String)

fun logFeat4Event1(event: Feat4AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat4Event2(event: Feat4AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat4Event3(event: Feat4AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat4Event4(event: Feat4AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat4Event5(event: Feat4AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat4Event6(event: Feat4AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat4Event7(event: Feat4AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat4Event8(event: Feat4AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat4Event9(event: Feat4AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat4Event10(event: Feat4AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat4Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat4Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat4(u: com.romix.core.model.CoreUser): Feat4Projection1 =
    Feat4Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<com.romix.core.model.CoreUser>): List<Feat4Projection1> {
    val list = java.util.ArrayList<Feat4Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat4(u)
    }
    return list
}
