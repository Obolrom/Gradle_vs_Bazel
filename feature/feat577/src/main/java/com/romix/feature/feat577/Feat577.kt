package com.romix.feature.feat577

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat577Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat577UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat577FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat577UserSummary
)

data class Feat577UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat577NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat577Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat577Config = Feat577Config()
) {

    fun loadSnapshot(userId: Long): Feat577NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat577NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat577UserSummary {
        return Feat577UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat577FeedItem> {
        val result = java.util.ArrayList<Feat577FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat577FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat577UiMapper {

    fun mapToUi(model: List<Feat577FeedItem>): Feat577UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat577UiModel(
            header = UiText("Feat577 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat577UiModel =
        Feat577UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat577UiModel =
        Feat577UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat577UiModel =
        Feat577UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat577Service(
    private val repository: Feat577Repository,
    private val uiMapper: Feat577UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat577UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat577UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat577UserItem1(val user: CoreUser, val label: String)
data class Feat577UserItem2(val user: CoreUser, val label: String)
data class Feat577UserItem3(val user: CoreUser, val label: String)
data class Feat577UserItem4(val user: CoreUser, val label: String)
data class Feat577UserItem5(val user: CoreUser, val label: String)
data class Feat577UserItem6(val user: CoreUser, val label: String)
data class Feat577UserItem7(val user: CoreUser, val label: String)
data class Feat577UserItem8(val user: CoreUser, val label: String)
data class Feat577UserItem9(val user: CoreUser, val label: String)
data class Feat577UserItem10(val user: CoreUser, val label: String)

data class Feat577StateBlock1(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock2(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock3(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock4(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock5(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock6(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock7(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock8(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock9(val state: Feat577UiModel, val checksum: Int)
data class Feat577StateBlock10(val state: Feat577UiModel, val checksum: Int)

fun buildFeat577UserItem(user: CoreUser, index: Int): Feat577UserItem1 {
    return Feat577UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat577StateBlock(model: Feat577UiModel): Feat577StateBlock1 {
    return Feat577StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat577UserSummary> {
    val list = java.util.ArrayList<Feat577UserSummary>(users.size)
    for (user in users) {
        list += Feat577UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat577UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat577UiModel {
    val summaries = (0 until count).map {
        Feat577UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat577UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat577UiModel> {
    val models = java.util.ArrayList<Feat577UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat577AnalyticsEvent1(val name: String, val value: String)
data class Feat577AnalyticsEvent2(val name: String, val value: String)
data class Feat577AnalyticsEvent3(val name: String, val value: String)
data class Feat577AnalyticsEvent4(val name: String, val value: String)
data class Feat577AnalyticsEvent5(val name: String, val value: String)
data class Feat577AnalyticsEvent6(val name: String, val value: String)
data class Feat577AnalyticsEvent7(val name: String, val value: String)
data class Feat577AnalyticsEvent8(val name: String, val value: String)
data class Feat577AnalyticsEvent9(val name: String, val value: String)
data class Feat577AnalyticsEvent10(val name: String, val value: String)

fun logFeat577Event1(event: Feat577AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat577Event2(event: Feat577AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat577Event3(event: Feat577AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat577Event4(event: Feat577AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat577Event5(event: Feat577AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat577Event6(event: Feat577AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat577Event7(event: Feat577AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat577Event8(event: Feat577AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat577Event9(event: Feat577AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat577Event10(event: Feat577AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat577Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat577Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat577(u: CoreUser): Feat577Projection1 =
    Feat577Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat577Projection1> {
    val list = java.util.ArrayList<Feat577Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat577(u)
    }
    return list
}
