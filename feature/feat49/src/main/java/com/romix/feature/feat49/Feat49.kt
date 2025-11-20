package com.romix.feature.feat49

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat49Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat49UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat49FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat49UserSummary
)

data class Feat49UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat49NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat49Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat49Config = Feat49Config()
) {

    fun loadSnapshot(userId: Long): Feat49NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat49NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat49UserSummary {
        return Feat49UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat49FeedItem> {
        val result = java.util.ArrayList<Feat49FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat49FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat49UiMapper {

    fun mapToUi(model: List<Feat49FeedItem>): Feat49UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat49UiModel(
            header = UiText("Feat49 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat49UiModel =
        Feat49UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat49UiModel =
        Feat49UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat49UiModel =
        Feat49UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat49Service(
    private val repository: Feat49Repository,
    private val uiMapper: Feat49UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat49UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat49UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat49UserItem1(val user: CoreUser, val label: String)
data class Feat49UserItem2(val user: CoreUser, val label: String)
data class Feat49UserItem3(val user: CoreUser, val label: String)
data class Feat49UserItem4(val user: CoreUser, val label: String)
data class Feat49UserItem5(val user: CoreUser, val label: String)
data class Feat49UserItem6(val user: CoreUser, val label: String)
data class Feat49UserItem7(val user: CoreUser, val label: String)
data class Feat49UserItem8(val user: CoreUser, val label: String)
data class Feat49UserItem9(val user: CoreUser, val label: String)
data class Feat49UserItem10(val user: CoreUser, val label: String)

data class Feat49StateBlock1(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock2(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock3(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock4(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock5(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock6(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock7(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock8(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock9(val state: Feat49UiModel, val checksum: Int)
data class Feat49StateBlock10(val state: Feat49UiModel, val checksum: Int)

fun buildFeat49UserItem(user: CoreUser, index: Int): Feat49UserItem1 {
    return Feat49UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat49StateBlock(model: Feat49UiModel): Feat49StateBlock1 {
    return Feat49StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat49UserSummary> {
    val list = java.util.ArrayList<Feat49UserSummary>(users.size)
    for (user in users) {
        list += Feat49UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat49UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat49UiModel {
    val summaries = (0 until count).map {
        Feat49UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat49UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat49UiModel> {
    val models = java.util.ArrayList<Feat49UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat49AnalyticsEvent1(val name: String, val value: String)
data class Feat49AnalyticsEvent2(val name: String, val value: String)
data class Feat49AnalyticsEvent3(val name: String, val value: String)
data class Feat49AnalyticsEvent4(val name: String, val value: String)
data class Feat49AnalyticsEvent5(val name: String, val value: String)
data class Feat49AnalyticsEvent6(val name: String, val value: String)
data class Feat49AnalyticsEvent7(val name: String, val value: String)
data class Feat49AnalyticsEvent8(val name: String, val value: String)
data class Feat49AnalyticsEvent9(val name: String, val value: String)
data class Feat49AnalyticsEvent10(val name: String, val value: String)

fun logFeat49Event1(event: Feat49AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat49Event2(event: Feat49AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat49Event3(event: Feat49AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat49Event4(event: Feat49AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat49Event5(event: Feat49AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat49Event6(event: Feat49AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat49Event7(event: Feat49AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat49Event8(event: Feat49AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat49Event9(event: Feat49AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat49Event10(event: Feat49AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat49Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat49Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat49(u: CoreUser): Feat49Projection1 =
    Feat49Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat49Projection1> {
    val list = java.util.ArrayList<Feat49Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat49(u)
    }
    return list
}
