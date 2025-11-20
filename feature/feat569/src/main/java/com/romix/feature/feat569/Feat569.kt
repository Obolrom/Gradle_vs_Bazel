package com.romix.feature.feat569

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat569Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat569UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat569FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat569UserSummary
)

data class Feat569UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat569NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat569Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat569Config = Feat569Config()
) {

    fun loadSnapshot(userId: Long): Feat569NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat569NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat569UserSummary {
        return Feat569UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat569FeedItem> {
        val result = java.util.ArrayList<Feat569FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat569FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat569UiMapper {

    fun mapToUi(model: List<Feat569FeedItem>): Feat569UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat569UiModel(
            header = UiText("Feat569 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat569UiModel =
        Feat569UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat569UiModel =
        Feat569UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat569UiModel =
        Feat569UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat569Service(
    private val repository: Feat569Repository,
    private val uiMapper: Feat569UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat569UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat569UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat569UserItem1(val user: CoreUser, val label: String)
data class Feat569UserItem2(val user: CoreUser, val label: String)
data class Feat569UserItem3(val user: CoreUser, val label: String)
data class Feat569UserItem4(val user: CoreUser, val label: String)
data class Feat569UserItem5(val user: CoreUser, val label: String)
data class Feat569UserItem6(val user: CoreUser, val label: String)
data class Feat569UserItem7(val user: CoreUser, val label: String)
data class Feat569UserItem8(val user: CoreUser, val label: String)
data class Feat569UserItem9(val user: CoreUser, val label: String)
data class Feat569UserItem10(val user: CoreUser, val label: String)

data class Feat569StateBlock1(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock2(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock3(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock4(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock5(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock6(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock7(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock8(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock9(val state: Feat569UiModel, val checksum: Int)
data class Feat569StateBlock10(val state: Feat569UiModel, val checksum: Int)

fun buildFeat569UserItem(user: CoreUser, index: Int): Feat569UserItem1 {
    return Feat569UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat569StateBlock(model: Feat569UiModel): Feat569StateBlock1 {
    return Feat569StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat569UserSummary> {
    val list = java.util.ArrayList<Feat569UserSummary>(users.size)
    for (user in users) {
        list += Feat569UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat569UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat569UiModel {
    val summaries = (0 until count).map {
        Feat569UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat569UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat569UiModel> {
    val models = java.util.ArrayList<Feat569UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat569AnalyticsEvent1(val name: String, val value: String)
data class Feat569AnalyticsEvent2(val name: String, val value: String)
data class Feat569AnalyticsEvent3(val name: String, val value: String)
data class Feat569AnalyticsEvent4(val name: String, val value: String)
data class Feat569AnalyticsEvent5(val name: String, val value: String)
data class Feat569AnalyticsEvent6(val name: String, val value: String)
data class Feat569AnalyticsEvent7(val name: String, val value: String)
data class Feat569AnalyticsEvent8(val name: String, val value: String)
data class Feat569AnalyticsEvent9(val name: String, val value: String)
data class Feat569AnalyticsEvent10(val name: String, val value: String)

fun logFeat569Event1(event: Feat569AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat569Event2(event: Feat569AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat569Event3(event: Feat569AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat569Event4(event: Feat569AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat569Event5(event: Feat569AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat569Event6(event: Feat569AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat569Event7(event: Feat569AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat569Event8(event: Feat569AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat569Event9(event: Feat569AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat569Event10(event: Feat569AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat569Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat569Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat569(u: CoreUser): Feat569Projection1 =
    Feat569Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat569Projection1> {
    val list = java.util.ArrayList<Feat569Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat569(u)
    }
    return list
}
