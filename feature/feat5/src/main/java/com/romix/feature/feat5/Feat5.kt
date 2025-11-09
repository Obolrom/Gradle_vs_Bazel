package com.romix.feature.feat5

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat5Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat5UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat5FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat5UserSummary
)

data class Feat5UiModel(
    val header: com.romix.core.ui.UiText,
    val items: List<com.romix.core.ui.UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat5NetworkSnapshot(
    val users: List<com.romix.core.network.ApiUserDto>,
    val posts: List<com.romix.core.network.ApiPostDto>,
    val rawHash: Int
)

class Feat5Repository(
    private val api: com.romix.core.network.FakeApiService = com.romix.core.network.FakeApiService(
        com.romix.core.network.FakeNetworkClient()
    ),
    private val config: Feat5Config = Feat5Config()
) {

    fun loadSnapshot(userId: Long): Feat5NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat5NetworkSnapshot(
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

    fun toUserSummary(coreUser: com.romix.core.model.CoreUser): Feat5UserSummary {
        return Feat5UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = com.romix.core.model.computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<com.romix.core.model.CoreUser>): List<Feat5FeedItem> {
        val result = java.util.ArrayList<Feat5FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat5FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat5UiMapper {

    fun mapToUi(model: List<Feat5FeedItem>): Feat5UiModel {
        val items = model.mapIndexed { index, item ->
            com.romix.core.ui.UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat5UiModel(
            header = com.romix.core.ui.UiText("Feat5 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat5UiModel =
        Feat5UiModel(
            header = com.romix.core.ui.UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat5UiModel =
        Feat5UiModel(
            header = com.romix.core.ui.UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat5UiModel =
        Feat5UiModel(
            header = com.romix.core.ui.UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat5Service(
    private val repository: Feat5Repository,
    private val uiMapper: Feat5UiMapper,
    private val networkClient: com.romix.core.network.FakeNetworkClient,
    private val apiService: com.romix.core.network.FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat5UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat5UiModel {
        val users = (0 until usersCount).map {
            com.romix.core.model.CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat5UserItem1(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem2(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem3(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem4(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem5(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem6(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem7(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem8(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem9(val user: com.romix.core.model.CoreUser, val label: String)
data class Feat5UserItem10(val user: com.romix.core.model.CoreUser, val label: String)

data class Feat5StateBlock1(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock2(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock3(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock4(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock5(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock6(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock7(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock8(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock9(val state: Feat5UiModel, val checksum: Int)
data class Feat5StateBlock10(val state: Feat5UiModel, val checksum: Int)

fun buildFeat5UserItem(user: com.romix.core.model.CoreUser, index: Int): Feat5UserItem1 {
    return Feat5UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat5StateBlock(model: Feat5UiModel): Feat5StateBlock1 {
    return Feat5StateBlock1(
        state = model,
        checksum = com.romix.core.model.computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<com.romix.core.model.CoreUser>): List<Feat5UserSummary> {
    val list = java.util.ArrayList<Feat5UserSummary>(users.size)
    for (user in users) {
        list += Feat5UserSummary(
            id = user.id,
            name = user.name,
            checksum = com.romix.core.model.computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat5UserSummary>): List<com.romix.core.ui.UiListItem> {
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

fun createLargeUiModel(count: Int): Feat5UiModel {
    val summaries = (0 until count).map {
        Feat5UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat5UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat5UiModel> {
    val models = java.util.ArrayList<Feat5UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat5AnalyticsEvent1(val name: String, val value: String)
data class Feat5AnalyticsEvent2(val name: String, val value: String)
data class Feat5AnalyticsEvent3(val name: String, val value: String)
data class Feat5AnalyticsEvent4(val name: String, val value: String)
data class Feat5AnalyticsEvent5(val name: String, val value: String)
data class Feat5AnalyticsEvent6(val name: String, val value: String)
data class Feat5AnalyticsEvent7(val name: String, val value: String)
data class Feat5AnalyticsEvent8(val name: String, val value: String)
data class Feat5AnalyticsEvent9(val name: String, val value: String)
data class Feat5AnalyticsEvent10(val name: String, val value: String)

fun logFeat5Event1(event: Feat5AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat5Event2(event: Feat5AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat5Event3(event: Feat5AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat5Event4(event: Feat5AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat5Event5(event: Feat5AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat5Event6(event: Feat5AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat5Event7(event: Feat5AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat5Event8(event: Feat5AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat5Event9(event: Feat5AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat5Event10(event: Feat5AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat5Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat5Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat5(u: com.romix.core.model.CoreUser): Feat5Projection1 =
    Feat5Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<com.romix.core.model.CoreUser>): List<Feat5Projection1> {
    val list = java.util.ArrayList<Feat5Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat5(u)
    }
    return list
}
