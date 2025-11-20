package com.romix.feature.feat405

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat405Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat405UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat405FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat405UserSummary
)

data class Feat405UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat405NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat405Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat405Config = Feat405Config()
) {

    fun loadSnapshot(userId: Long): Feat405NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat405NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat405UserSummary {
        return Feat405UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat405FeedItem> {
        val result = java.util.ArrayList<Feat405FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat405FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat405UiMapper {

    fun mapToUi(model: List<Feat405FeedItem>): Feat405UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat405UiModel(
            header = UiText("Feat405 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat405UiModel =
        Feat405UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat405UiModel =
        Feat405UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat405UiModel =
        Feat405UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat405Service(
    private val repository: Feat405Repository,
    private val uiMapper: Feat405UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat405UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat405UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat405UserItem1(val user: CoreUser, val label: String)
data class Feat405UserItem2(val user: CoreUser, val label: String)
data class Feat405UserItem3(val user: CoreUser, val label: String)
data class Feat405UserItem4(val user: CoreUser, val label: String)
data class Feat405UserItem5(val user: CoreUser, val label: String)
data class Feat405UserItem6(val user: CoreUser, val label: String)
data class Feat405UserItem7(val user: CoreUser, val label: String)
data class Feat405UserItem8(val user: CoreUser, val label: String)
data class Feat405UserItem9(val user: CoreUser, val label: String)
data class Feat405UserItem10(val user: CoreUser, val label: String)

data class Feat405StateBlock1(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock2(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock3(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock4(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock5(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock6(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock7(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock8(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock9(val state: Feat405UiModel, val checksum: Int)
data class Feat405StateBlock10(val state: Feat405UiModel, val checksum: Int)

fun buildFeat405UserItem(user: CoreUser, index: Int): Feat405UserItem1 {
    return Feat405UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat405StateBlock(model: Feat405UiModel): Feat405StateBlock1 {
    return Feat405StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat405UserSummary> {
    val list = java.util.ArrayList<Feat405UserSummary>(users.size)
    for (user in users) {
        list += Feat405UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat405UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat405UiModel {
    val summaries = (0 until count).map {
        Feat405UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat405UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat405UiModel> {
    val models = java.util.ArrayList<Feat405UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat405AnalyticsEvent1(val name: String, val value: String)
data class Feat405AnalyticsEvent2(val name: String, val value: String)
data class Feat405AnalyticsEvent3(val name: String, val value: String)
data class Feat405AnalyticsEvent4(val name: String, val value: String)
data class Feat405AnalyticsEvent5(val name: String, val value: String)
data class Feat405AnalyticsEvent6(val name: String, val value: String)
data class Feat405AnalyticsEvent7(val name: String, val value: String)
data class Feat405AnalyticsEvent8(val name: String, val value: String)
data class Feat405AnalyticsEvent9(val name: String, val value: String)
data class Feat405AnalyticsEvent10(val name: String, val value: String)

fun logFeat405Event1(event: Feat405AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat405Event2(event: Feat405AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat405Event3(event: Feat405AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat405Event4(event: Feat405AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat405Event5(event: Feat405AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat405Event6(event: Feat405AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat405Event7(event: Feat405AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat405Event8(event: Feat405AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat405Event9(event: Feat405AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat405Event10(event: Feat405AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat405Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat405Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat405(u: CoreUser): Feat405Projection1 =
    Feat405Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat405Projection1> {
    val list = java.util.ArrayList<Feat405Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat405(u)
    }
    return list
}
