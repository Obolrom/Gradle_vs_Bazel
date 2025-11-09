package com.romix.feature.feat10

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat10Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat10UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat10FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat10UserSummary
)

data class Feat10UiModel(
    val header: com.romix.core.ui.UiText,
    val items: List<com.romix.core.ui.UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat10NetworkSnapshot(
    val users: List<com.romix.core.network.ApiUserDto>,
    val posts: List<com.romix.core.network.ApiPostDto>,
    val rawHash: Int
)

class Feat10Repository(
    private val api: com.romix.core.network.FakeApiService = com.romix.core.network.FakeApiService(
        com.romix.core.network.FakeNetworkClient()
    ),
    private val config: Feat10Config = Feat10Config()
) {

    fun loadSnapshot(userId: Long): Feat10NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat10NetworkSnapshot(
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

    fun toUserSummary(coreUser: com.romix.core.model.CoreUser): Feat10UserSummary {
        return Feat10UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = com.romix.core.model.computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<com.romix.core.model.CoreUser>): List<Feat10FeedItem> {
        val result = java.util.ArrayList<Feat10FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat10FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat10UiMapper {

    fun mapToUi(model: List<Feat10FeedItem>): Feat10UiModel {
        val items = model.mapIndexed { index, item ->
            com.romix.core.ui.UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat10UiModel(
            header = com.romix.core.ui.UiText("Feat10 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat10UiModel =
        Feat10UiModel(
            header = com.romix.core.ui.UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat10UiModel =
        Feat10UiModel(
            header = com.romix.core.ui.UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat10UiModel =
        Feat10UiModel(
            header = com.romix.core.ui.UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat10Service(
    private val repository: Feat10Repository,
    private val uiMapper: Feat10UiMapper,
    private val networkClient: com.romix.core.network.FakeNetworkClient,
    private val apiService: com.romix.core.network.FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat10UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat10UiModel {
        val users = (0 until usersCount).map {
            com.romix.core.model.CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat10UserItem1(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem2(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem3(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem4(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem5(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem6(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem7(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem8(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem9(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat10UserItem10(val user: com.romix.core.model.CoreUser, val label: String)

data class Feat10StateBlock1(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock2(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock3(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock4(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock5(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock6(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock7(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock8(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock9(val state: Feat10UiModel, val checksum: Int)
data class Feat10StateBlock10(val state: Feat10UiModel, val checksum: Int)

fun buildFeat10UserItem(user: com.romix.core.model.CoreUser, index: Int): Feat10UserItem1 {
    return Feat10UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat10StateBlock(model: Feat10UiModel): Feat10StateBlock1 {
    return Feat10StateBlock1(
        state = model,
        checksum = com.romix.core.model.computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<com.romix.core.model.CoreUser>): List<Feat10UserSummary> {
    val list = java.util.ArrayList<Feat10UserSummary>(users.size)
    for (user in users) {
        list += Feat10UserSummary(
            id = user.id,
            name = user.name,
            checksum = com.romix.core.model.computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat10UserSummary>): List<com.romix.core.ui.UiListItem> {
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

fun createLargeUiModel(count: Int): Feat10UiModel {
    val summaries = (0 until count).map {
        Feat10UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat10UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat10UiModel> {
    val models = java.util.ArrayList<Feat10UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat10AnalyticsEvent1(val name: String, val value: String)
data class Feat10AnalyticsEvent2(val name: String, val value: String)
data class Feat10AnalyticsEvent3(val name: String, val value: String)
data class Feat10AnalyticsEvent4(val name: String, val value: String)
data class Feat10AnalyticsEvent5(val name: String, val value: String)
data class Feat10AnalyticsEvent6(val name: String, val value: String)
data class Feat10AnalyticsEvent7(val name: String, val value: String)
data class Feat10AnalyticsEvent8(val name: String, val value: String)
data class Feat10AnalyticsEvent9(val name: String, val value: String)
data class Feat10AnalyticsEvent10(val name: String, val value: String)

fun logFeat10Event1(event: Feat10AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat10Event2(event: Feat10AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat10Event3(event: Feat10AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat10Event4(event: Feat10AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat10Event5(event: Feat10AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat10Event6(event: Feat10AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat10Event7(event: Feat10AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat10Event8(event: Feat10AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat10Event9(event: Feat10AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat10Event10(event: Feat10AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat10Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat10Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat10(u: com.romix.core.model.CoreUser): Feat10Projection1 =
    Feat10Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<com.romix.core.model.CoreUser>): List<Feat10Projection1> {
    val list = java.util.ArrayList<Feat10Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat10(u)
    }
    return list
}
