package com.romix.feature.feat78

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat78Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat78UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat78FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat78UserSummary
)

data class Feat78UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat78NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat78Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat78Config = Feat78Config()
) {

    fun loadSnapshot(userId: Long): Feat78NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat78NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat78UserSummary {
        return Feat78UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat78FeedItem> {
        val result = java.util.ArrayList<Feat78FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat78FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat78UiMapper {

    fun mapToUi(model: List<Feat78FeedItem>): Feat78UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat78UiModel(
            header = UiText("Feat78 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat78UiModel =
        Feat78UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat78UiModel =
        Feat78UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat78UiModel =
        Feat78UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat78Service(
    private val repository: Feat78Repository,
    private val uiMapper: Feat78UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat78UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat78UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat78UserItem1(val user: CoreUser, val label: String)
data class Feat78UserItem2(val user: CoreUser, val label: String)
data class Feat78UserItem3(val user: CoreUser, val label: String)
data class Feat78UserItem4(val user: CoreUser, val label: String)
data class Feat78UserItem5(val user: CoreUser, val label: String)
data class Feat78UserItem6(val user: CoreUser, val label: String)
data class Feat78UserItem7(val user: CoreUser, val label: String)
data class Feat78UserItem8(val user: CoreUser, val label: String)
data class Feat78UserItem9(val user: CoreUser, val label: String)
data class Feat78UserItem10(val user: CoreUser, val label: String)

data class Feat78StateBlock1(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock2(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock3(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock4(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock5(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock6(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock7(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock8(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock9(val state: Feat78UiModel, val checksum: Int)
data class Feat78StateBlock10(val state: Feat78UiModel, val checksum: Int)

fun buildFeat78UserItem(user: CoreUser, index: Int): Feat78UserItem1 {
    return Feat78UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat78StateBlock(model: Feat78UiModel): Feat78StateBlock1 {
    return Feat78StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat78UserSummary> {
    val list = java.util.ArrayList<Feat78UserSummary>(users.size)
    for (user in users) {
        list += Feat78UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat78UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat78UiModel {
    val summaries = (0 until count).map {
        Feat78UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat78UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat78UiModel> {
    val models = java.util.ArrayList<Feat78UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat78AnalyticsEvent1(val name: String, val value: String)
data class Feat78AnalyticsEvent2(val name: String, val value: String)
data class Feat78AnalyticsEvent3(val name: String, val value: String)
data class Feat78AnalyticsEvent4(val name: String, val value: String)
data class Feat78AnalyticsEvent5(val name: String, val value: String)
data class Feat78AnalyticsEvent6(val name: String, val value: String)
data class Feat78AnalyticsEvent7(val name: String, val value: String)
data class Feat78AnalyticsEvent8(val name: String, val value: String)
data class Feat78AnalyticsEvent9(val name: String, val value: String)
data class Feat78AnalyticsEvent10(val name: String, val value: String)

fun logFeat78Event1(event: Feat78AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat78Event2(event: Feat78AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat78Event3(event: Feat78AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat78Event4(event: Feat78AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat78Event5(event: Feat78AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat78Event6(event: Feat78AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat78Event7(event: Feat78AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat78Event8(event: Feat78AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat78Event9(event: Feat78AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat78Event10(event: Feat78AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat78Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat78Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat78(u: CoreUser): Feat78Projection1 =
    Feat78Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat78Projection1> {
    val list = java.util.ArrayList<Feat78Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat78(u)
    }
    return list
}
