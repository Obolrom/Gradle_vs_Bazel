package com.romix.feature.feat8

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat8Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat8UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat8FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat8UserSummary
)

data class Feat8UiModel(
    val header: com.romix.core.ui.UiText,
    val items: List<com.romix.core.ui.UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat8NetworkSnapshot(
    val users: List<com.romix.core.network.ApiUserDto>,
    val posts: List<com.romix.core.network.ApiPostDto>,
    val rawHash: Int
)

class Feat8Repository(
    private val api: com.romix.core.network.FakeApiService = com.romix.core.network.FakeApiService(
        com.romix.core.network.FakeNetworkClient()
    ),
    private val config: Feat8Config = Feat8Config()
) {

    fun loadSnapshot(userId: Long): Feat8NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat8NetworkSnapshot(
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

    fun toUserSummary(coreUser: com.romix.core.model.CoreUser): Feat8UserSummary {
        return Feat8UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = com.romix.core.model.computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<com.romix.core.model.CoreUser>): List<Feat8FeedItem> {
        val result = java.util.ArrayList<Feat8FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat8FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat8UiMapper {

    fun mapToUi(model: List<Feat8FeedItem>): Feat8UiModel {
        val items = model.mapIndexed { index, item ->
            com.romix.core.ui.UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat8UiModel(
            header = com.romix.core.ui.UiText("Feat8 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat8UiModel =
        Feat8UiModel(
            header = com.romix.core.ui.UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat8UiModel =
        Feat8UiModel(
            header = com.romix.core.ui.UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat8UiModel =
        Feat8UiModel(
            header = com.romix.core.ui.UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat8Service(
    private val repository: Feat8Repository,
    private val uiMapper: Feat8UiMapper,
    private val networkClient: com.romix.core.network.FakeNetworkClient,
    private val apiService: com.romix.core.network.FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat8UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat8UiModel {
        val users = (0 until usersCount).map {
            com.romix.core.model.CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat8UserItem1(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem2(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem3(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem4(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem5(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem6(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem7(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem8(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem9(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat8UserItem10(val user: com.romix.core.model.CoreUser, val label: String)

data class Feat8StateBlock1(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock2(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock3(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock4(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock5(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock6(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock7(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock8(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock9(val state: Feat8UiModel, val checksum: Int)
data class Feat8StateBlock10(val state: Feat8UiModel, val checksum: Int)

fun buildFeat8UserItem(user: com.romix.core.model.CoreUser, index: Int): Feat8UserItem1 {
    return Feat8UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat8StateBlock(model: Feat8UiModel): Feat8StateBlock1 {
    return Feat8StateBlock1(
        state = model,
        checksum = com.romix.core.model.computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<com.romix.core.model.CoreUser>): List<Feat8UserSummary> {
    val list = java.util.ArrayList<Feat8UserSummary>(users.size)
    for (user in users) {
        list += Feat8UserSummary(
            id = user.id,
            name = user.name,
            checksum = com.romix.core.model.computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat8UserSummary>): List<com.romix.core.ui.UiListItem> {
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

fun createLargeUiModel(count: Int): Feat8UiModel {
    val summaries = (0 until count).map {
        Feat8UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat8UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat8UiModel> {
    val models = java.util.ArrayList<Feat8UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat8AnalyticsEvent1(val name: String, val value: String)
data class Feat8AnalyticsEvent2(val name: String, val value: String)
data class Feat8AnalyticsEvent3(val name: String, val value: String)
data class Feat8AnalyticsEvent4(val name: String, val value: String)
data class Feat8AnalyticsEvent5(val name: String, val value: String)
data class Feat8AnalyticsEvent6(val name: String, val value: String)
data class Feat8AnalyticsEvent7(val name: String, val value: String)
data class Feat8AnalyticsEvent8(val name: String, val value: String)
data class Feat8AnalyticsEvent9(val name: String, val value: String)
data class Feat8AnalyticsEvent10(val name: String, val value: String)

fun logFeat8Event1(event: Feat8AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat8Event2(event: Feat8AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat8Event3(event: Feat8AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat8Event4(event: Feat8AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat8Event5(event: Feat8AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat8Event6(event: Feat8AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat8Event7(event: Feat8AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat8Event8(event: Feat8AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat8Event9(event: Feat8AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat8Event10(event: Feat8AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat8Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat8Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat8(u: com.romix.core.model.CoreUser): Feat8Projection1 =
    Feat8Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<com.romix.core.model.CoreUser>): List<Feat8Projection1> {
    val list = java.util.ArrayList<Feat8Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat8(u)
    }
    return list
}
