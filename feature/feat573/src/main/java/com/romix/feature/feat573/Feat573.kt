package com.romix.feature.feat573

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat573Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat573UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat573FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat573UserSummary
)

data class Feat573UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat573NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat573Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat573Config = Feat573Config()
) {

    fun loadSnapshot(userId: Long): Feat573NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat573NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat573UserSummary {
        return Feat573UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat573FeedItem> {
        val result = java.util.ArrayList<Feat573FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat573FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat573UiMapper {

    fun mapToUi(model: List<Feat573FeedItem>): Feat573UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat573UiModel(
            header = UiText("Feat573 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat573UiModel =
        Feat573UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat573UiModel =
        Feat573UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat573UiModel =
        Feat573UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat573Service(
    private val repository: Feat573Repository,
    private val uiMapper: Feat573UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat573UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat573UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat573UserItem1(val user: CoreUser, val label: String)
data class Feat573UserItem2(val user: CoreUser, val label: String)
data class Feat573UserItem3(val user: CoreUser, val label: String)
data class Feat573UserItem4(val user: CoreUser, val label: String)
data class Feat573UserItem5(val user: CoreUser, val label: String)
data class Feat573UserItem6(val user: CoreUser, val label: String)
data class Feat573UserItem7(val user: CoreUser, val label: String)
data class Feat573UserItem8(val user: CoreUser, val label: String)
data class Feat573UserItem9(val user: CoreUser, val label: String)
data class Feat573UserItem10(val user: CoreUser, val label: String)

data class Feat573StateBlock1(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock2(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock3(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock4(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock5(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock6(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock7(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock8(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock9(val state: Feat573UiModel, val checksum: Int)
data class Feat573StateBlock10(val state: Feat573UiModel, val checksum: Int)

fun buildFeat573UserItem(user: CoreUser, index: Int): Feat573UserItem1 {
    return Feat573UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat573StateBlock(model: Feat573UiModel): Feat573StateBlock1 {
    return Feat573StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat573UserSummary> {
    val list = java.util.ArrayList<Feat573UserSummary>(users.size)
    for (user in users) {
        list += Feat573UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat573UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat573UiModel {
    val summaries = (0 until count).map {
        Feat573UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat573UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat573UiModel> {
    val models = java.util.ArrayList<Feat573UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat573AnalyticsEvent1(val name: String, val value: String)
data class Feat573AnalyticsEvent2(val name: String, val value: String)
data class Feat573AnalyticsEvent3(val name: String, val value: String)
data class Feat573AnalyticsEvent4(val name: String, val value: String)
data class Feat573AnalyticsEvent5(val name: String, val value: String)
data class Feat573AnalyticsEvent6(val name: String, val value: String)
data class Feat573AnalyticsEvent7(val name: String, val value: String)
data class Feat573AnalyticsEvent8(val name: String, val value: String)
data class Feat573AnalyticsEvent9(val name: String, val value: String)
data class Feat573AnalyticsEvent10(val name: String, val value: String)

fun logFeat573Event1(event: Feat573AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat573Event2(event: Feat573AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat573Event3(event: Feat573AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat573Event4(event: Feat573AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat573Event5(event: Feat573AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat573Event6(event: Feat573AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat573Event7(event: Feat573AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat573Event8(event: Feat573AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat573Event9(event: Feat573AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat573Event10(event: Feat573AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat573Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat573Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat573(u: CoreUser): Feat573Projection1 =
    Feat573Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat573Projection1> {
    val list = java.util.ArrayList<Feat573Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat573(u)
    }
    return list
}
