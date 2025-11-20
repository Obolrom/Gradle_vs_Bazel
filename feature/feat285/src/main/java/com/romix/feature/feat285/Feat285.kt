package com.romix.feature.feat285

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat285Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat285UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat285FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat285UserSummary
)

data class Feat285UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat285NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat285Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat285Config = Feat285Config()
) {

    fun loadSnapshot(userId: Long): Feat285NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat285NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat285UserSummary {
        return Feat285UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat285FeedItem> {
        val result = java.util.ArrayList<Feat285FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat285FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat285UiMapper {

    fun mapToUi(model: List<Feat285FeedItem>): Feat285UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat285UiModel(
            header = UiText("Feat285 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat285UiModel =
        Feat285UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat285UiModel =
        Feat285UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat285UiModel =
        Feat285UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat285Service(
    private val repository: Feat285Repository,
    private val uiMapper: Feat285UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat285UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat285UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat285UserItem1(val user: CoreUser, val label: String)
data class Feat285UserItem2(val user: CoreUser, val label: String)
data class Feat285UserItem3(val user: CoreUser, val label: String)
data class Feat285UserItem4(val user: CoreUser, val label: String)
data class Feat285UserItem5(val user: CoreUser, val label: String)
data class Feat285UserItem6(val user: CoreUser, val label: String)
data class Feat285UserItem7(val user: CoreUser, val label: String)
data class Feat285UserItem8(val user: CoreUser, val label: String)
data class Feat285UserItem9(val user: CoreUser, val label: String)
data class Feat285UserItem10(val user: CoreUser, val label: String)

data class Feat285StateBlock1(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock2(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock3(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock4(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock5(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock6(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock7(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock8(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock9(val state: Feat285UiModel, val checksum: Int)
data class Feat285StateBlock10(val state: Feat285UiModel, val checksum: Int)

fun buildFeat285UserItem(user: CoreUser, index: Int): Feat285UserItem1 {
    return Feat285UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat285StateBlock(model: Feat285UiModel): Feat285StateBlock1 {
    return Feat285StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat285UserSummary> {
    val list = java.util.ArrayList<Feat285UserSummary>(users.size)
    for (user in users) {
        list += Feat285UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat285UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat285UiModel {
    val summaries = (0 until count).map {
        Feat285UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat285UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat285UiModel> {
    val models = java.util.ArrayList<Feat285UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat285AnalyticsEvent1(val name: String, val value: String)
data class Feat285AnalyticsEvent2(val name: String, val value: String)
data class Feat285AnalyticsEvent3(val name: String, val value: String)
data class Feat285AnalyticsEvent4(val name: String, val value: String)
data class Feat285AnalyticsEvent5(val name: String, val value: String)
data class Feat285AnalyticsEvent6(val name: String, val value: String)
data class Feat285AnalyticsEvent7(val name: String, val value: String)
data class Feat285AnalyticsEvent8(val name: String, val value: String)
data class Feat285AnalyticsEvent9(val name: String, val value: String)
data class Feat285AnalyticsEvent10(val name: String, val value: String)

fun logFeat285Event1(event: Feat285AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat285Event2(event: Feat285AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat285Event3(event: Feat285AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat285Event4(event: Feat285AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat285Event5(event: Feat285AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat285Event6(event: Feat285AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat285Event7(event: Feat285AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat285Event8(event: Feat285AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat285Event9(event: Feat285AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat285Event10(event: Feat285AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat285Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat285Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat285(u: CoreUser): Feat285Projection1 =
    Feat285Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat285Projection1> {
    val list = java.util.ArrayList<Feat285Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat285(u)
    }
    return list
}
