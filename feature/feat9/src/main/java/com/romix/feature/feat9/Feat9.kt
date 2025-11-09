package com.romix.feature.feat9

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat9Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat9UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat9FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat9UserSummary
)

data class Feat9UiModel(
    val header: com.romix.core.ui.UiText,
    val items: List<com.romix.core.ui.UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat9NetworkSnapshot(
    val users: List<com.romix.core.network.ApiUserDto>,
    val posts: List<com.romix.core.network.ApiPostDto>,
    val rawHash: Int
)

class Feat9Repository(
    private val api: com.romix.core.network.FakeApiService = com.romix.core.network.FakeApiService(
        com.romix.core.network.FakeNetworkClient()
    ),
    private val config: Feat9Config = Feat9Config()
) {

    fun loadSnapshot(userId: Long): Feat9NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat9NetworkSnapshot(
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

    fun toUserSummary(coreUser: com.romix.core.model.CoreUser): Feat9UserSummary {
        return Feat9UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = com.romix.core.model.computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<com.romix.core.model.CoreUser>): List<Feat9FeedItem> {
        val result = java.util.ArrayList<Feat9FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat9FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat9UiMapper {

    fun mapToUi(model: List<Feat9FeedItem>): Feat9UiModel {
        val items = model.mapIndexed { index, item ->
            com.romix.core.ui.UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat9UiModel(
            header = com.romix.core.ui.UiText("Feat9 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat9UiModel =
        Feat9UiModel(
            header = com.romix.core.ui.UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat9UiModel =
        Feat9UiModel(
            header = com.romix.core.ui.UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat9UiModel =
        Feat9UiModel(
            header = com.romix.core.ui.UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat9Service(
    private val repository: Feat9Repository,
    private val uiMapper: Feat9UiMapper,
    private val networkClient: com.romix.core.network.FakeNetworkClient,
    private val apiService: com.romix.core.network.FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat9UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat9UiModel {
        val users = (0 until usersCount).map {
            com.romix.core.model.CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat9UserItem1(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem2(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem3(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem4(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem5(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem6(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem7(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem8(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem9(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat9UserItem10(val user: com.romix.core.model.CoreUser, val label: String)

data class Feat9StateBlock1(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock2(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock3(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock4(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock5(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock6(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock7(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock8(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock9(val state: Feat9UiModel, val checksum: Int)
data class Feat9StateBlock10(val state: Feat9UiModel, val checksum: Int)

fun buildFeat9UserItem(user: com.romix.core.model.CoreUser, index: Int): Feat9UserItem1 {
    return Feat9UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat9StateBlock(model: Feat9UiModel): Feat9StateBlock1 {
    return Feat9StateBlock1(
        state = model,
        checksum = com.romix.core.model.computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<com.romix.core.model.CoreUser>): List<Feat9UserSummary> {
    val list = java.util.ArrayList<Feat9UserSummary>(users.size)
    for (user in users) {
        list += Feat9UserSummary(
            id = user.id,
            name = user.name,
            checksum = com.romix.core.model.computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat9UserSummary>): List<com.romix.core.ui.UiListItem> {
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

fun createLargeUiModel(count: Int): Feat9UiModel {
    val summaries = (0 until count).map {
        Feat9UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat9UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat9UiModel> {
    val models = java.util.ArrayList<Feat9UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat9AnalyticsEvent1(val name: String, val value: String)
data class Feat9AnalyticsEvent2(val name: String, val value: String)
data class Feat9AnalyticsEvent3(val name: String, val value: String)
data class Feat9AnalyticsEvent4(val name: String, val value: String)
data class Feat9AnalyticsEvent5(val name: String, val value: String)
data class Feat9AnalyticsEvent6(val name: String, val value: String)
data class Feat9AnalyticsEvent7(val name: String, val value: String)
data class Feat9AnalyticsEvent8(val name: String, val value: String)
data class Feat9AnalyticsEvent9(val name: String, val value: String)
data class Feat9AnalyticsEvent10(val name: String, val value: String)

fun logFeat9Event1(event: Feat9AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat9Event2(event: Feat9AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat9Event3(event: Feat9AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat9Event4(event: Feat9AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat9Event5(event: Feat9AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat9Event6(event: Feat9AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat9Event7(event: Feat9AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat9Event8(event: Feat9AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat9Event9(event: Feat9AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat9Event10(event: Feat9AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat9Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat9Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat9(u: com.romix.core.model.CoreUser): Feat9Projection1 =
    Feat9Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<com.romix.core.model.CoreUser>): List<Feat9Projection1> {
    val list = java.util.ArrayList<Feat9Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat9(u)
    }
    return list
}
